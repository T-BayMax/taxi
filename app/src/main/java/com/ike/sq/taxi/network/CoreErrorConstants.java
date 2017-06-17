package com.ike.sq.taxi.network;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态码以及状态
 */
public class CoreErrorConstants {
    public static Map<Integer, String> errors;

    static {
        errors = new HashMap<>();
        errors.put(200, "成功");
        errors.put(0, "请求失败");
        errors.put(-1, "请求非post");
        errors.put(100, "已存在（用户已注册.....）");
        errors.put(101, "非VIP");
        errors.put(102, "已举报");
        errors.put(1000, "请求字段不能空");
        errors.put(1001, "推荐信息不存在");
        errors.put(1002, "禁止登陆");
        errors.put(1003, "密码出错");
        errors.put(1004, "信誉值不够");
        errors.put(1005, "用户已是群管理员");
        errors.put(1006, "用户不是群管理");
        errors.put(1007, "群主审核已拒绝");
        errors.put(1008, "群主审核已忽略");
        errors.put(1009, "退出群");
        errors.put(1010, "群活动非法操作");
        errors.put(1011, "群活动还未开始");
        errors.put(1012, "群活动结束");
        errors.put(1013, "群活动人数已满");
        errors.put(1014, "群活动已加入");
        errors.put(1015, "请上传文件");
        errors.put(1016, "群投票时间已结束");
        errors.put(1017, "群已投票");
        errors.put(1018, "群投票已关闭或已投票已失效");
        errors.put(1019, "群投票没有该选项");
        errors.put(1020, "已认领");
        errors.put(1021, "用户已被认领");
        errors.put(1022, "用户未填写认领问题");
        errors.put(1023, "身份证号码错误");
        errors.put(1024, "未查询到个人身份证信息");
        errors.put(1025, "求助用户已回答");
        errors.put(1026, "求助用户已采纳");
        errors.put(1027, "求助采纳失败");
        errors.put(1028, "求助非法操作");
        errors.put(1029, "点赞失败");
        errors.put(1030, "请勿重新点赞");
        errors.put(1031, "已收藏");
        errors.put(1032, "平台活动已报名");
        errors.put(1033, "平台活动报名失败");
        errors.put(1034, "每日已签到");
        errors.put(1035, "每日签到失败");
        errors.put(1036, "公益活动已报名");
        errors.put(1037, "公益活动报名失败");
        errors.put(1038, "上传视频文件不符合");
        errors.put(1038, "还有订单正在使用");

    }
}
