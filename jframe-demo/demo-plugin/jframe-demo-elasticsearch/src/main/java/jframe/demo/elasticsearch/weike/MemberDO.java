package jframe.demo.elasticsearch.weike;

import java.util.List;

public class MemberDO extends BaseMemberDO {

  private static final long serialVersionUID = 7899212399009930709L;

  private long sellerId;

  /**
   * 买家会员ID
   */
  private long buyerId;

  /**
   * 最后一次交易的订单号
   */
  private long bizOrderId;

  /**
   * 交易关闭金额
   */
  private double closeTradeAmount;

  /**
   * 交易关闭的笔数
   */
  private long closeTradeCount;

  /**
   * 会员等级，0：无会员等级，1：普通会员，2：高级会员，3：VIP会员， 4：至尊VIP会员
   */
  private long grade;

  /**
   * 平均客单价.
   */
  private double avgPrice;

  /**
   * 城市
   */
  private String city;

  /**
   * 会员拥有的所有分组
   */
  private String groupIds;

  /**
   * 交易关闭的宝贝件数
   */
  private long itemCloseCount;

  /**
   * 购买的宝贝件数
   */
  private long itemNum;

  /**
   * 最后交易时间
   */
  private long lastTradeTime;

  /**
   * 北京=1,天津=2,河北省=3,山西省=4,内蒙古自治区=5,辽宁省=6,吉林省=7,黑龙江省=8,上海=9,江苏省=10,浙江省=11,安徽省=
   * 12,福建省=13,江西省=14,山东省=15,河南省=16,湖北省=17,湖南省=18,
   * 广东省=19,广西壮族自治区=20,海南省=21,重庆=
   * 22,四川省=23,贵州省=24,云南省=25,西藏自治区26,陕西省=27,甘肃省=28,
   * 青海省=29,宁夏回族自治区=30,新疆维吾尔自治区=31,台湾省=32,香港特别行政区=33,澳门特别行政区=34,海外=35
   */
  private long province;

  /**
   * 关系来源，1交易成功，2未成交
   */
  private long relationSource;

  /**
   * 显示会员的状态，normal正常，delete被买家删除，blacklist黑名单
   */
  private String status;

  /**
   * 交易成功的金额
   */
  private double tradeAmount;

  /**
   * 交易成功笔数
   */
  private long tradeCount;

  /**
   * 修改时间
   */
  private long modified;
  /**
   * 单品
   */
  private List<Long> items;
  /**
   * 交易来源，对应 {@link TradeDO#getTradeFrom()}
   */
  private List<String> tradeFroms;

  public long getSellerId() {
    return sellerId;
  }

  public void setSellerId(long sellerId) {
    this.sellerId = sellerId;
  }

  public long getBuyerId() {
    return buyerId;
  }

  public void setBuyerId(long buyerId) {
    this.buyerId = buyerId;
  }

  public long getBizOrderId() {
    return bizOrderId;
  }

  public void setBizOrderId(long bizOrderId) {
    this.bizOrderId = bizOrderId;
  }

  public long getCloseTradeCount() {
    return closeTradeCount;
  }

  public void setCloseTradeCount(long closeTradeCount) {
    this.closeTradeCount = closeTradeCount;
  }

  public long getGrade() {
    return grade;
  }

  public void setGrade(long grade) {
    this.grade = grade;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(String groupIds) {
    this.groupIds = groupIds;
  }

  public long getItemCloseCount() {
    return itemCloseCount;
  }

  public void setItemCloseCount(long itemCloseCount) {
    this.itemCloseCount = itemCloseCount;
  }

  public long getItemNum() {
    return itemNum;
  }

  public void setItemNum(long itemNum) {
    this.itemNum = itemNum;
  }

  public long getLastTradeTime() {
    return lastTradeTime;
  }

  public void setLastTradeTime(long lastTradeTime) {
    this.lastTradeTime = lastTradeTime;
  }

  public long getProvince() {
    return province;
  }

  public void setProvince(long province) {
    this.province = province;
  }

  public long getRelationSource() {
    return relationSource;
  }

  public void setRelationSource(long relationSource) {
    this.relationSource = relationSource;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getTradeCount() {
    return tradeCount;
  }

  public void setTradeCount(long tradeCount) {
    this.tradeCount = tradeCount;
  }

  public double getCloseTradeAmount() {
    return closeTradeAmount;
  }

  public void setCloseTradeAmount(double closeTradeAmount) {
    this.closeTradeAmount = closeTradeAmount;
  }

  public double getAvgPrice() {
    return avgPrice;
  }

  public void setAvgPrice(double avgPrice) {
    this.avgPrice = avgPrice;
  }

  public double getTradeAmount() {
    return tradeAmount;
  }

  public void setTradeAmount(double tradeAmount) {
    this.tradeAmount = tradeAmount;
  }

  public long getModified() {
    return modified;
  }

  public void setModified(long modified) {
    this.modified = modified;
  }

  public List<String> getTradeFroms() {
    return tradeFroms;
  }

  public void setTradeFroms(List<String> tradeFroms) {
    this.tradeFroms = tradeFroms;
  }

  public List<Long> getItems() {
    return items;
  }

  public void setItems(List<Long> items) {
    this.items = items;
  }
}
