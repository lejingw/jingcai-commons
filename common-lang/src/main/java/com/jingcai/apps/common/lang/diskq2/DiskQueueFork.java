package com.jingcai.apps.common.lang.diskq2;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiskQueueFork implements Closeable {
	public static final int FORK_OFFSET = 100;  
	private int MAX_FORK_COUNT = 1000; 
	private volatile int maxIndex = 0; // 用于优化不需要扫描所有Fork项

	private final DiskQueueIndex rawIndex;
	private RandomAccessFile forkFile;
	private FileChannel fileChannel;
	private MappedByteBuffer mappedBuffer;
	private Map<String, ForkedIndex> readIndice = new ConcurrentHashMap<String, ForkedIndex>();
	
	public DiskQueueFork(String forkFilePath, final DiskQueueIndex rawIndex) {
		this.rawIndex = rawIndex;
		File file = new File(forkFilePath);
		final int mapSize = ForkedIndex.SIZE * MAX_FORK_COUNT;
		try {
			if (file.exists()) {
				this.forkFile = new RandomAccessFile(file, "rw");
				this.fileChannel = forkFile.getChannel();
				this.mappedBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, mapSize);
				this.mappedBuffer = mappedBuffer.load();
				this.maxIndex = mappedBuffer.getInt();
				loadForkedIndice();
			} else {
				this.forkFile = new RandomAccessFile(file, "rw");
				this.fileChannel = forkFile.getChannel();
				this.mappedBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, mapSize);
				putMaxIndex(0);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private void loadForkedIndice(){
		for(int i=0; i<maxIndex; i++){
			ForkedIndex forked = new ForkedIndex(i, mappedBuffer);
			if(forked.isActive()){
				readIndice.put(forked.getForkId(), forked);
			}
		}
	}

	public void putMaxIndex(int maxIndex) {
		this.mappedBuffer.position(0);
		this.mappedBuffer.putInt(maxIndex);
		this.maxIndex = maxIndex;
	}
	
    public int getMaxIndex(){
    	return this.maxIndex;
    }

	public ForkedIndex fork(String forkId) {
		if (readIndice.containsKey(forkId)) {
			return readIndice.get(forkId);
		}
		int index = firstAvailableIndex();
		ForkedIndex forkedIndex = new ForkedIndex(index, mappedBuffer);
		forkedIndex.putFlag((byte)1); 
		forkedIndex.putReadNum(rawIndex.getReadNum());
		forkedIndex.putReadPos(rawIndex.getReadPosition());
		forkedIndex.putReadCount(rawIndex.getReadCounter());
		forkedIndex.putForkId(forkId); 
		
		readIndice.put(forkId, forkedIndex);
		return forkedIndex;
	}

	public void removeFork(ForkedIndex forkedIndex) {
		readIndice.remove(forkedIndex.getForkId());
		forkedIndex.putFlag((byte)0);
	}

	public ForkedIndex forkedIndex(String forkId) {
		return readIndice.get(forkId);
	}
	
	public int firstAvailableIndex(){ 
		int res = maxIndex; 
		for(int i=0; i<maxIndex; i++){
			int pos = FORK_OFFSET + i*ForkedIndex.SIZE;
			mappedBuffer.position(pos);
			byte flag = mappedBuffer.get();
			if(!ForkedIndex.isActive(flag)){
				return i;
			}
		}
		
		if(res == maxIndex){
			if(maxIndex == MAX_FORK_COUNT){
				throw new IllegalStateException("reached max fork limit");
			}
			maxIndex++;
		}
		return res;
	}

	public ForkedIndex minForkedIndex() {
		ForkedIndex min = null;
		Iterator<ForkedIndex> iter = readIndice.values().iterator();
		while (iter.hasNext()) {
			ForkedIndex curr = iter.next();
			if (min == null) {
				min = curr;
				continue;
			}
			if (curr.getReadNum() > min.getReadNum()) {
				continue;
			}
			if (curr.getReadNum() < min.getReadNum()) {
				min = curr;
				continue;
			}
			// equals
			if (curr.getReadPos() < min.getReadPos()) {
				min = curr;
			}
		}
		return min;
	}
	public void sync() {
        if (mappedBuffer != null) {
        	mappedBuffer.force();
        }
    }
    
	@Override
	public void close() throws IOException {
		sync();
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
        	@SuppressWarnings("restriction")
            public Object run() {
                try {
                    Method getCleanerMethod = mappedBuffer.getClass().getMethod("cleaner");
                    getCleanerMethod.setAccessible(true); 
					sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(mappedBuffer);
                    cleaner.clean();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
	}
	
	public static void main(String[] args) throws Exception {
		DiskQueueIndex rawIndex = new DiskQueueIndex("MyMQ", "store");
		DiskQueueFork fork = new DiskQueueFork("store/MyMQ.fork", rawIndex);
		
		ForkedIndex forkedIndex = fork.fork("hongleiming"); 
		
		fork.removeFork(forkedIndex);
		
		fork.close();
		System.out.println("==done==");
	}
}