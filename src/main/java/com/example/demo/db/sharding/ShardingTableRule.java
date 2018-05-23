package com.example.demo.db.sharding;

import com.example.demo.exception.AppBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShardingTableRule {

    private static final Logger logger = LoggerFactory.getLogger(ShardingTableRule.class);

    private String logicTableName;

    private List<String> actualTableNames;

    public static final int TABLE_COUNT = 32;


    public ShardingTableRule(String logicTableName) {
        this.logicTableName = logicTableName;

        actualTableNames = IntStream.range(0, TABLE_COUNT).mapToObj(x -> logicTableName + "_"
                + (x<10?"0" + x : String.valueOf(x))).collect(Collectors.toList());
    }

    public String getLogicTableName() {
        return logicTableName;
    }

    public List<String> getActualTableNames() {
        return actualTableNames;
    }

    public static String generateTableName(String logicTableName, String id) {
        int index = uuidToTableIndex(id);
        String tableNum = index < 10 ? "0" + index : String.valueOf(index);
        //String tableNum = String.valueOf(index);
        return logicTableName + "_" + tableNum;
    }

    /**
     * #MD5(大写UserId) 的后四位 转十进制 % 128（取模） ，分为128个表
     * select conv(right(md5(UPPER(trim(BorrowerUserId))),4),16,10) % 128
     * @param id
     * @return
     */
    public static int uuidToTableIndex(String id) {
        return conv(right(md5(id.trim().toUpperCase()), 4)) % TABLE_COUNT;
    }

    private static String md5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return DatatypeConverter.printHexBinary(md5.digest(str.getBytes("UTF-8")));
        } catch (Exception e) {
            String errorMessage = String.format("生成md5字符串的时候发生错误, 字符串: %s, 错误信息: %s", str, e.getMessage());
            logger.error(errorMessage, e);
            throw new AppBusinessException(errorMessage);
        }
    }

    private static String right(String str, int length) {
        return str.substring(str.length() - length, str.length());
    }

    private static int conv(String str) {
        return Integer.parseInt(str, 16);
    }


}
