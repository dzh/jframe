/**
 * 
 */
package jframe.rongcloud.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rong.ApiHttpClient;
import io.rong.models.FormatType;
import io.rong.models.SdkHttpResult;
import io.rong.util.GsonUtil;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.rongcloud.RongcloudConf;
import jframe.rongcloud.RongcloudPlugin;
import jframe.rongcloud.service.RongcloudService;

/**
 * @author dzh
 * @date Feb 14, 2016 11:07:45 PM
 * @since 1.0
 */
@Injector
public class RongcloudServiceImpl implements RongcloudService {

    static Logger LOG = LoggerFactory.getLogger(RongcloudServiceImpl.class);

    @InjectPlugin
    static RongcloudPlugin Plugin;

    RongcloudConf conf = new RongcloudConf();

    static String FILE_RC = "file.rongcloud";

    @Start
    void start() {
        String f = Plugin.getConfig(FILE_RC);
        try {
            conf.init(f);

            LOG.info("Start RongcloudService Successfully!");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            LOG.error("Start RongcloudService Failure! {} -> {}", FILE_RC, f);
        }
    }

    @Override
    public String getToken(String id, Map<String, String> req) {
        SdkHttpResult r = null;
        try {
            r = ApiHttpClient.getToken(conf.getConf(id, RongcloudConf.K_APP_KEY),
                    conf.getConf(id, RongcloudConf.K_APP_SECRET), req.get(F_usrId), req.get(F_name),
                    req.get(F_portraitUri), FormatType.json);
            if (200 == r.getHttpCode()) {
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) GsonUtil.fromJson(r.getResult(), HashMap.class);
                return map.get(F_token);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        LOG.error("RongcloudService.getToken error! id->{} req->{} r->{}", id, req, r);
        return null;
    }

    @Override
    public boolean refreshUsr(String id, Map<String, String> req) {
        SdkHttpResult r = null;
        try {
            r = ApiHttpClient.refreshUser(conf.getConf(id, RongcloudConf.K_APP_KEY),
                    conf.getConf(id, RongcloudConf.K_APP_SECRET), req.get(F_usrId), req.get(F_name),
                    req.get(F_portraitUri), FormatType.json);
            if ("200".equals(r.getHttpCode())) {
                return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        LOG.error("RongcloudService.refreshUsr error! id->{} req->{} r->{}", id, req, r);
        return false;
    }

    @Override
    public boolean createGroup(String id, Map<String, String> req) {
        SdkHttpResult r = null;
        try {
            List<String> ids = Arrays.asList(req.get(F_usrList).split("_"));

            r = ApiHttpClient.createGroup(conf.getConf(id, RongcloudConf.K_APP_KEY),
                    conf.getConf(id, RongcloudConf.K_APP_SECRET), ids, req.get(F_groupId), req.get(F_groupName),
                    FormatType.json);
            if ("200".equals(r.getHttpCode())) {
                return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        LOG.error("RongcloudService.createGroup error! id->{} req->{} r->{}", id, req, r);
        return false;
    }

    @Override
    public boolean joinGroupBatch(String id, Map<String, String> req) {
        SdkHttpResult r = null;
        try {
            List<String> ids = Arrays.asList(req.get(F_usrList).split("_"));

            r = ApiHttpClient.joinGroupBatch(conf.getConf(id, RongcloudConf.K_APP_KEY),
                    conf.getConf(id, RongcloudConf.K_APP_SECRET), ids, req.get(F_groupId), req.get(F_groupName),
                    FormatType.json);
            if ("200".equals(r.getHttpCode())) {
                return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        LOG.error("RongcloudService.joinGroupBatch error! id->{} req->{} r->{}", id, req, r);
        return false;
    }

    @Override
    public boolean quitGroup(String id, Map<String, String> req) {
        SdkHttpResult r = null;
        try {
            r = ApiHttpClient.quitGroup(conf.getConf(id, RongcloudConf.K_APP_KEY),
                    conf.getConf(id, RongcloudConf.K_APP_SECRET), req.get(F_usrId), req.get(F_groupId),
                    FormatType.json);
            if ("200".equals(r.getHttpCode())) {
                return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        LOG.error("RongcloudService.quitGroup error! id->{} req->{} r->{}", id, req, r);
        return false;
    }

    @Override
    public boolean dismissGroup(String id, Map<String, String> req) {
        SdkHttpResult r = null;
        try {
            r = ApiHttpClient.dismissGroup(conf.getConf(id, RongcloudConf.K_APP_KEY),
                    conf.getConf(id, RongcloudConf.K_APP_SECRET), req.get(F_usrId), req.get(F_groupId),
                    FormatType.json);
            if ("200".equals(r.getHttpCode())) {
                return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        LOG.error("RongcloudService.dismissGroup error! id->{} req->{} r->{}", id, req, r);
        return false;
    }

    @Override
    public boolean refreshGroupInfo(String id, Map<String, String> req) {
        SdkHttpResult r = null;
        try {
            r = ApiHttpClient.refreshGroupInfo(conf.getConf(id, RongcloudConf.K_APP_KEY),
                    conf.getConf(id, RongcloudConf.K_APP_SECRET), req.get(F_groupId), req.get(F_groupName),
                    FormatType.json);
            if ("200".equals(r.getHttpCode())) {
                return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        LOG.error("RongcloudService.refreshGroupInfo error! id->{} req->{} r->{}", id, req, r);
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> queryGroupUserList(String id, Map<String, String> req) {
        SdkHttpResult r = null;
        try {
            r = ApiHttpClient.queryGroupUserList(conf.getConf(id, RongcloudConf.K_APP_KEY),
                    conf.getConf(id, RongcloudConf.K_APP_SECRET), req.get(F_groupId), FormatType.json);
            if ("200".equals(r.getHttpCode())) {
                Map<String, String> map = (Map<String, String>) GsonUtil.fromJson(r.getResult(), HashMap.class);
                String users = map.get(F_users);
                if (users != null) {
                    List<Map<String, String>> usrList = (List<Map<String, String>>) GsonUtil.fromJson(users,
                            ArrayList.class);
                    List<String> usrIds = new ArrayList<String>(usrList.size());
                    for (Map<String, String> m : usrList) {
                        usrIds.add(m.get(F_id));
                    }
                    return usrIds;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        LOG.error("RongcloudService.queryGroupUserList error! id->{} req->{} r->{}", id, req, r);
        return Collections.emptyList();
    }

}
