package com.jingcai.apps.common.lang.string;

/**
 * Created by lejing on 15/7/17.
 */
public class StringUtilBiz {
    /**
     * 隐藏电话号码中间的4位数字
     * @param phone
     * @return
     */
    public static String hidePhone(String phone) {
        if (StringUtils.isNotEmpty(phone) && phone.length() == 11) {
            String start = phone.substring(0, 3);
            String end = phone.substring(8, 11);
            phone = start + "*****" + end;
        }
        return phone;
    }

    /**
     * 金额字符串取整
     * @param money
     * @return
     */
    public static String getIntValue(String money) {
        if (StringUtils.isNotEmpty(money)) {
            Double dMoney = Double.parseDouble(money);
            Integer iMoney = dMoney.intValue();
            money = String.valueOf(iMoney);
        }
        return money;
    }
    /**
     * 隐藏姓名中的最后一个字符
     * @param name
     * @return
     */
    public static String hideName(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        } else {
            return name.substring(0, name.length() - 1) + "*";
        }
    }

    /**
     * 隐藏身份证号码，保留前后四位
     * @param idno
     * @return
     */
    public static String hideIdno(String idno) {
        if (StringUtils.isEmpty(idno)) {
            return idno;
        } else {
            if (idno.length() == 18) {
                return idno.substring(0, 4) + "**********" + idno.substring(14, 18);
            } else {
                return idno.substring(0, 4) + "*******" + idno.substring(11, 15);
            }
        }
    }
}
