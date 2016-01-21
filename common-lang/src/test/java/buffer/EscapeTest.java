package buffer;

import com.jingcai.apps.common.lang.encrypt.Encodes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * Created by lejing on 16/1/18.
 */
public class EscapeTest {
	@Test
	public void test1(){
		String html = "<a href=\"http://www.baidu.com\">这是内容部分.</a>";
		html = Encodes.escapeHtml(html);
		System.out.println(html);
		System.out.println(html = Encodes.unescapeHtml(html));
		System.out.println(html = Encodes.unescapeHtml(html));
		System.out.println(Jsoup.parse(html).text());
		System.out.println(Encodes.unescapeHtml("文本1&mdash;&mdash;文本2"));
	}

	@Test
	public void test2(){
		String html = "<html><head><title>First parse</title></head><body><p>ParseHTML into a doc.</p></body></html>";
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		System.out.println(doc.text());
	}
	@Test
	public void test3(){
		String html = "<h1>这是内容部分1</h1><a href=\"http://www.baidu.com\">这是内容部分2</a>这是内容部分3";
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		System.out.println(doc.text());
	}
	@Test
	public void test4(){
		String html = "【工作名称】：睿丁英语电话客服【薪资待遇】：140元/天含2个任务+提成+平台补贴【招募人数】：6人【工作时间】：10:00&mdash;18:00 每个周一至周五【工作地点】：朝阳望京六佰本【工作内容】：电话邀约【工作要求】：主要负责电话邀约，邀约周边家长带着孩子上门听体验课，有简单话术培训。上午10点到12点是试用期，没有通过试用期的当天无工资，过了试用期的发当天工资。 超过或不足每个邀约20元。【薪资结算】：次日结 &nbsp;";
		html = Encodes.unescapeHtml(html);
		System.out.println(html);
		Document doc = Jsoup.parse(html);
		System.out.println(doc.text());
//		System.out.println(html = Encodes.unescapeHtml(html));
//		System.out.println(html = Encodes.unescapeHtml(html));
//		System.out.println(Jsoup.parse(html).text());
//		System.out.println(Encodes.unescapeHtml("文本1&mdash;&mdash;文本2"));
	}
}
