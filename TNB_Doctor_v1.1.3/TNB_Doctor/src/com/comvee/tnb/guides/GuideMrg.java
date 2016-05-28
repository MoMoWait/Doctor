package com.comvee.tnb.guides;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.record.RecordMainFragment;
import com.comvee.tnb.ui.record.RecordSugarInputNewFrag;
import com.comvee.tool.UserMrg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GuideMrg {

    private static GuideMrg mInstance;
    private IndexTaskInfo mIndexInfo;
    private HashMap<Integer, GuideQuesInfo> mQuesMap;
    private ArrayList<JSONObject> mResults = new ArrayList<JSONObject>();
    ;

    public static GuideMrg getInstance() {
        return mInstance == null ? mInstance = new GuideMrg() : mInstance;
    }

    ;

    /**
     * 加载运动任务推荐
     *
     * @param activity
     * @param guideItem
     */
    private void loadSportsValue(final BaseFragment activity, final IndexTaskInfo guideItem) {
        //
        activity.showProgressDialog(activity.getString(R.string.msg_loading));
        new AsyncTask<Integer, Integer, GuideFoodInfo>() {

            @Override
            protected GuideFoodInfo doInBackground(Integer... params) {
                //
                GuideFoodInfo info;
                ComveeHttp http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_GUIDE_TASK_SOPRTS);
                http.setPostValueForKey("taskCode", String.valueOf(guideItem.getTaskCode()));
                http.setPostValueForKey("seq", String.valueOf(guideItem.getSeq()));
                String result = http.startSyncRequestString();
                info = DataParser.createFoodRecommend(result);

                return info;
            }

            @Override
            protected void onPostExecute(GuideFoodInfo info) {
                super.onPostExecute(info);
                activity.cancelProgressDialog();
                if (info == null) {
                    activity.showToast(activity.getResources().getString(R.string.error));
                } else {
                    // ComveeHttp.clearCache(activity.getActivity(),
                    // UserMrg.getCacheKey(ConfigUrlMrg.NEW_INDEX_DETAIL));
                    FragmentMrg.toFragment(activity.getActivity(), GuideHealthFoodRecommendFrag.newInstance(info), true, true);
                }
            }
        }.execute();

    }

    /**
     * 加载全面检查的内容
     *
     * @param activity
     * @param guideItem
     */
    private void loadFullCheck(final BaseFragment activity, final IndexTaskInfo guideItem) {
        //
        activity.showProgressDialog(activity.getString(R.string.msg_loading));
        new AsyncTask<Integer, Integer, GuideFullCheckInfo>() {

            @Override
            protected GuideFullCheckInfo doInBackground(Integer... params) {
                GuideFullCheckInfo info = null;
                //
                try {
                    ComveeHttp http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_GUIDE_TASK_FULL_CHECK);
                    http.setPostValueForKey("taskCode", String.valueOf(guideItem.getTaskCode()));
                    http.setPostValueForKey("seq", String.valueOf(guideItem.getSeq()));
                    String result = http.startSyncRequestString();
                    info = DataParser.createTaskFullCheckInfo(result);
                    info.setTaskCode(String.valueOf(guideItem.getTaskCode()));
                    info.setTaskStatus(String.valueOf(guideItem.getStatus()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return info;
            }

            protected void onPostExecute(GuideFullCheckInfo result) {

                activity.cancelProgressDialog();
                if (result == null) {
                    activity.showToast(activity.getResources().getString(R.string.error));
                } else {
                    // ComveeHttp.clearCache(activity.getActivity(),
                    // UserMrg.getCacheKey(ConfigUrlMrg.NEW_INDEX_DETAIL));
                    FragmentMrg.toFragment(activity.getActivity(), GuideHealthFullCheck.newInstance(result), true, true);
                }
            }

            ;
        }.execute();

    }

    // 加载血糖监测的任务
    private void loadBloodSugarTask(final BaseFragment activity, final IndexTaskInfo guideItem) {
        //
        activity.showProgressDialog(activity.getString(R.string.msg_loading));
        new AsyncTask<Integer, Integer, GuideSugarMonitorInfo>() {

            @Override
            protected GuideSugarMonitorInfo doInBackground(Integer... params) {
                //
                GuideSugarMonitorInfo info = null;
                try {
                    ComveeHttp http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_GUIDE_TASK_BLOOD_SUGAR_MONITOR);
                    http.setPostValueForKey("taskCode", String.valueOf(guideItem.getTaskCode()));
                    http.setPostValueForKey("seq", String.valueOf(guideItem.getSeq()));
                    String result = http.startSyncRequestString();
                    info = DataParser.createBloodSuageInfo(result);
                } catch (Exception e) {

                    e.printStackTrace();
                }
                return info;
            }

            protected void onPostExecute(GuideSugarMonitorInfo mInfo) {

                activity.cancelProgressDialog();
                if (mInfo == null) {
                    // Log.e("tnb", "接口错误");
                    activity.showToast(activity.getResources().getString(R.string.error));
                } else {
                    // ComveeHttp.clearCache(activity.getActivity(),
                    // UserMrg.getCacheKey(ConfigUrlMrg.NEW_INDEX_DETAIL));
                    FragmentMrg.toFragment(activity.getActivity(), GuideHealthSugarMonitor.newInstance(mInfo), true, true);
                }
            }
        }.execute();

    }

    private void loadReadTask(final BaseFragment activity, final IndexTaskInfo guideItem) {

        activity.showProgressDialog(activity.getString(R.string.msg_loading));
        new AsyncTask<Integer, Integer, GuideBrowseInfo>() {

            @Override
            protected GuideBrowseInfo doInBackground(Integer... params) {
                GuideBrowseInfo after = null;
                ComveeHttp http = null;
                try {

                    switch (guideItem.getType()) {
                        case IndexTaskInfo.JUMP_BROWSE:
                        case IndexTaskInfo.JUMP_SPORTS_VALUE_HTML:
                            http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.READ_JOB_DETAIL);
                            http.setPostValueForKey("taskCode", String.valueOf(guideItem.getTaskCode()));
                            http.setPostValueForKey("taskSeq", String.valueOf(guideItem.getSeq()));
                            http.setPostValueForKey("task_status", String.valueOf(guideItem.getStatus()));// new
                            break;
                        case IndexTaskInfo.JUMP_NEW_SPORTS_VALUE_1:
                            http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.SPORE_REMIND);
                            http.setPostValueForKey("taskId", String.valueOf(guideItem.getTaskId()));
                            http.setPostValueForKey("seq", String.valueOf(guideItem.getSeq()));
                            http.setPostValueForKey("status", String.valueOf(guideItem.getStatus()));// new
                            break;
                        case IndexTaskInfo.JUMP_NEW_SPORTS_VALUE:
                            http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_SPORT_JOB);
                            http.setPostValueForKey("taskId", String.valueOf(guideItem.getTaskId()));
                            http.setPostValueForKey("seq", String.valueOf(guideItem.getSeq()));
                            http.setPostValueForKey("status", String.valueOf(guideItem.getStatus()));// new
                            break;
                        default:
                            break;
                    }

                    String result = http.startSyncRequestString();
                    after = DataParser.createTaskContentInfo(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return after;
            }

            protected void onPostExecute(GuideBrowseInfo result) {
                try {
                    activity.cancelProgressDialog();
                    if (result == null) {
                        activity.showToast(activity.getResources().getString(R.string.error));
                    } else {
                        // ComveeHttp.clearCache(activity.getActivity(),
                        // UserMrg.getCacheKey(ConfigUrlMrg.NEW_INDEX_DETAIL));
                        if (result.getPageType() == 11) {
                            FragmentMrg.toFragment(activity.getActivity(), GuideFoodResultFrag.newInstance(guideItem, result), false, true);
                        } else {
                            FragmentMrg.toFragment(activity.getActivity(), GuideHealthBrowseFrag.newInstance(guideItem, result, true), true, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        }.execute();

    }

    private void loadFoodRecommend(final BaseFragment activity, final IndexTaskInfo guideItem) {
        activity.showProgressDialog(activity.getString(R.string.msg_loading));
        new AsyncTask<Integer, Integer, GuideFoodInfo>() {

            @Override
            protected GuideFoodInfo doInBackground(Integer... params) {
                GuideFoodInfo after = null;
                try {
                    ComveeHttp http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_GUIDE_TASK_FOOD);
                    http.setPostValueForKey("taskCode", String.valueOf(guideItem.getTaskCode()));
                    http.setPostValueForKey("taskSeq", String.valueOf(guideItem.getSeq()));
                    String result = http.startSyncRequestString();
                    after = DataParser.createFoodRecommend(result);
                    // Log.i("", "result-->" + after.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return after;
            }

            protected void onPostExecute(GuideFoodInfo result) {

                activity.cancelProgressDialog();
                if (result == null) {
                    activity.showToast(activity.getResources().getString(R.string.error));
                } else {
                    // ComveeHttp.clearCache(activity.getActivity(),
                    // UserMrg.getCacheKey(ConfigUrlMrg.NEW_INDEX_DETAIL));
                    FragmentMrg.toFragment(activity.getActivity(), GuideHealthFoodRecommendFrag.newInstance(result), false, true);
                }
            }

            ;
        }.execute();

    }

    private void loadFoodRecipe(final BaseFragment activity, final IndexTaskInfo guideItem) {
        activity.showProgressDialog(activity.getString(R.string.msg_loading));
        new AsyncTask<Integer, Integer, GuideFoodRecipeInfo>() {

            @Override
            protected GuideFoodRecipeInfo doInBackground(Integer... params) {
                GuideFoodRecipeInfo after = null;
                try {
                    ComveeHttp http = null;
                    switch (guideItem.getType()) {
                        case IndexTaskInfo.JUMP_FOOD_RECIPE:
                            http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.GUIDE_RECIPE);
                            http.setPostValueForKey("taskCode", String.valueOf(guideItem.getTaskCode()));
                            http.setPostValueForKey("seq", String.valueOf(guideItem.getSeq()));
                            http.setPostValueForKey("task_status", String.valueOf(guideItem.getStatus()));// new
                            break;
                        case IndexTaskInfo.JUMP_NEW_FOOD_RECIOE:
                            http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_GUIDE_RECIPE);
                            http.setPostValueForKey("taskId", String.valueOf(guideItem.getTaskId()));
                            http.setPostValueForKey("seq", String.valueOf(guideItem.getSeq()));
                            http.setPostValueForKey("status", String.valueOf(guideItem.getStatus()));// new
                            break;
                        case IndexTaskInfo.JUMP_NEW_FOOD_RECIOE_INDEX:
                            http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_GUIDE_RECIPE_INDEX);
                            http.setPostValueForKey("task_code", String.valueOf(guideItem.getTaskCode()));
                            http.setPostValueForKey("seq", String.valueOf(guideItem.getSeq()));
                            // http.setPostValueForKey("task_status",
                            // String.valueOf(guideItem.getTaskStatus()));// new
                            break;
                        default:
                            break;
                    }

                    // Log.i("", guideItem + "");
                    String result = http.startSyncRequestString();
                    after = DataParser.createFoodRecipeInfo(result);
                    // Log.i("", "result-->" + after.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return after;
            }

            protected void onPostExecute(GuideFoodRecipeInfo result) {

                activity.cancelProgressDialog();
                if (result == null) {
                    activity.showToast(activity.getResources().getString(R.string.error));
                } else {
                    // ComveeHttp.clearCache(activity.getActivity(),
                    // UserMrg.getCacheKey(ConfigUrlMrg.NEW_INDEX_DETAIL));
                    if (result.getPagetype().equals("10")) {
                        FragmentMrg.toFragment(activity.getActivity(), GuideFoodResultFrag.newInstance(guideItem, result), false, true);
                    } else {
                        FragmentMrg.toFragment(activity.getActivity(), GuideHealthFoodRecipe.newInstance(guideItem, result), false, true);
                    }
                }
            }

            ;
        }.execute();

    }

    @SuppressLint("UseSparseArrays")
    private void loadQuesTask(final BaseFragment activity, final IndexTaskInfo guideItem) {
        activity.showProgressDialog(activity.getString(R.string.msg_loading));
        new AsyncTask<Integer, Integer, ArrayList<GuideQuesInfo>>() {

            @Override
            protected ArrayList<GuideQuesInfo> doInBackground(Integer... params) {

                ComveeHttp http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.GUIDE_JOB_DETAIL);
                http.setPostValueForKey("taskCode", String.valueOf(guideItem.getTaskCode()));
                String result = http.startSyncRequestString();
                ArrayList<GuideQuesInfo> infos = null;
                try {
                    infos = DataParser.createAssess(result);
                    mQuesMap = new HashMap<Integer, GuideQuesInfo>();
                    for (GuideQuesInfo info : infos) {
                        // Log.e("tnb", "题目：" + info.getTopicSeq());
                        mQuesMap.put(info.getTopicSeq(), info);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return infos;
            }

            protected void onPostExecute(ArrayList<GuideQuesInfo> result) {
                // mQues = result;

                activity.cancelProgressDialog();
                if (result == null) {
                    activity.showToast(R.string.time_out);
                } else {
                    // ComveeHttp.clearCache(activity.getActivity(),
                    // UserMrg.getCacheKey(ConfigUrlMrg.NEW_INDEX_DETAIL));
                    jumpQuesTask(activity, result.get(0));
                }
            }

            ;
        }.execute();

    }

    private void loadQuesResultAlready(final BaseFragment frag, final IndexTaskInfo guideItem) {
        frag.showProgressDialog(frag.getString(R.string.msg_loading));
        new ComveeTask<Object>() {

            @Override
            protected Object doInBackground() {
                try {
                    ComveeHttp http = null;
                    if (guideItem.getType() == 14) {
                        http = new ComveeHttp(frag.getActivity().getApplicationContext(), ConfigUrlMrg.FOOD_REMIND_TASK_RESULT);
                        http.setPostValueForKey("taskCode", String.valueOf(guideItem.getTaskCode()));
                        http.setPostValueForKey("status", String.valueOf(guideItem.getStatus()));
                    } else {
                        http = new ComveeHttp(frag.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_INDEX_TASK_OVER);
                        http.setPostValueForKey("task_id", String.valueOf(guideItem.getTaskId()));
                    }
                    String result = http.startSyncRequestString();
                    final ComveePacket packet = ComveePacket.fromJsonString(result);

                    if (packet.getResultCode() != 0) {
                        postError(packet);
                        return null;
                    }
                    GuideResultInfo info = null;
                    try {
                        info = DataParser.createAssessResultInfo(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }

                    return info;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                if (result instanceof GuideResultInfo) {
                    ComveeHttp.clearCache(frag.getActivity(), UserMrg.getCacheKey(ConfigUrlMrg.NEW_INDEX_DETAIL));
                    if (((GuideResultInfo) result).getPagetype() == 9) {
                        FragmentMrg.toFragment(frag.getActivity(), GuideNewResultFrag.newInstance(guideItem, (GuideResultInfo) result), true, true);
                    } else {
                        FragmentMrg.toFragment(frag.getActivity(), GuideHealthResultFrag.newInstance((GuideResultInfo) result), true, true);
                    }
                    // FragmentMrg.toFragment(frag.getActivity(),
                    // GuideHealthResultFrag.newInstance((GuideResultInfo)
                    // result), true, true);
                } else if (result == null) {
                    frag.showToast(R.string.time_out);
                }
                frag.cancelProgressDialog();
            }

        }.execute();

    }

    // 增加了从首页进入的结果页字段
    private void loadQuesResult(final BaseFragment activity, final IndexTaskInfo indexInfo) {

        activity.showProgressDialog(activity.getString(R.string.msg_loading));
        new AsyncTask<Integer, Integer, GuideResultInfo>() {

            @Override
            protected GuideResultInfo doInBackground(Integer... params) {
                String result;
                GuideResultInfo info = null;
                JSONArray array = new JSONArray();
                for (JSONObject obj : mResults) {
                    array.put(obj);
                }

                ComveeHttp http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.GUIDE_JOB_FINISH);
                http.setPostValueForKey("taskCode", String.valueOf(indexInfo.getTaskCode()));
                http.setPostValueForKey("param", array.toString());
                try {
                    result = http.startSyncRequestString();
                    // Log.e("tnb", result);
                    info = DataParser.createAssessResultInfo(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                return info;
            }

            protected void onPostExecute(GuideResultInfo result) {
                // mQues = result;
                activity.cancelProgressDialog();
                if (result == null) {
                    activity.showToast(R.string.time_out);
                } else {
                    if (result.getPagetype() == 9) {
                        FragmentMrg.toFragment(activity.getActivity(), GuideNewResultFrag.newInstance(indexInfo, result), true, true);
                    } else {
                        FragmentMrg.toFragment(activity.getActivity(), GuideHealthResultFrag.newInstance(result), true, true);
                    }
                }
                // jumpQuesTask(activity, result.get(0));
            }

            ;
        }.execute();

    }

    private void jumpQuesTask(final BaseFragment activity, GuideQuesInfo info) {

        if (info == null) {
            // activity.showToast("无题目");
            loadQuesResult(activity, mIndexInfo);
            return;
        }

        switch (info.getTopicType()) {
            case GuideQuesInfo.JUMP_CHOOSE_NUM:
                FragmentMrg.toFragment(activity.getActivity(), GuideHealthChooseNumFrag.newInstance(info), true, true);
                break;
            case GuideQuesInfo.JUMP_CHOOSE_TRHEE:
                FragmentMrg.toFragment(activity.getActivity(), GuideHealthChooseMoreFrag.newInstance(info), true, true);
                break;
            case GuideQuesInfo.JUMP_CHOOSE_TWO:
                FragmentMrg.toFragment(activity.getActivity(), GuideHealthChooseSingleQuesFrag.newInstance(info), true, true);
                break;
            case GuideQuesInfo.JUMP_CHOOSE_DATE:
                FragmentMrg.toFragment(activity.getActivity(), GuideHealthChooseDateFrag.newInstance(info), true, true);
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到下一题
     *
     * @param activity
     * @param info
     * @param seq
     * @param value
     */
    public void jumpNextQuesTask(final BaseFragment activity, GuideQuesInfo info, int seq, String value) {
        // Log.e("tnb", "跳题目：" + seq);
        // if(value==null){
        // loadQuesResultAlready(activity, mIndexInfo);
        // }
        try {
            JSONObject obj = new JSONObject();
            obj.put("itemvalue", value);
            obj.put("topicid", info.getTopicID());
            obj.put("topicseq", info.getTopicSeq());
            mResults.add(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        jumpQuesTask(activity, mQuesMap.get(seq));
    }

    public void jumpGuide(final BaseFragment activity, IndexTaskInfo guideItem) {

        switch (guideItem.getType()) {
            // case IndexTaskInfo.JUMP_BROWSE:
            // loadReadTask(activity, guideItem);
            // break;
            case IndexTaskInfo.JUMP_QUES:

                if (guideItem.getStatus() == 1 || guideItem.getStatus() == 1) {
                    loadQuesResultAlready(activity, guideItem);// 结果页
                } else {
                    mResults.clear();
                    mIndexInfo = guideItem;
                    loadQuesTask(activity, guideItem);
                }

                break;
            case IndexTaskInfo.JUMP_FULL_CHECK:
                loadFullCheck(activity, guideItem);
                // if(guideItem.getTaskStatus() == 1){
                //
                // }
                break;
            case IndexTaskInfo.JUMP_FOOD_RECOMMED:
                loadFoodRecommend(activity, guideItem);
                break;
            case IndexTaskInfo.JUMP_FOOD_RECIPE:
                loadFoodRecipe(activity, guideItem);
                break;

            case IndexTaskInfo.JUMP_SPORTS_VALUE:
                loadSportsValue(activity, guideItem);
                break;
            // case IndexTaskInfo.JUMP_SPORTS_VALUE_HTML:
            // loadReadTask(activity, guideItem);
            // break;
            case IndexTaskInfo.JUMP_BLOOD_SUGAR_MONITOR:
                if (guideItem.getStatus() == 1) {
                    activity.toFragment(RecordMainFragment.newInstance(true, 0, 0), true, true);
                } else {
                    loadBloodSugarTask(activity, guideItem);
                }
                break;

            case IndexTaskInfo.JUMP_BLOOD_SUGAR_TEST:
                if (guideItem.getStatus() == 1) {
                    activity.toFragment(RecordMainFragment.newInstance(true, 0, 0), true, true);
                } else {
                	FragmentMrg.toFragment(activity.getActivity(), RecordSugarInputNewFrag.class, null, true);
                }
                break;
            case IndexTaskInfo.JUMP_NEW_FOOD_RECIOE:
                loadFoodRecipe(activity, guideItem);
                break;
            case IndexTaskInfo.JUMP_NEW_FOOD_RECIOE_INDEX:
                loadFoodRecipe(activity, guideItem);
                break;
            case IndexTaskInfo.JUMP_SPORTS_VALUE_HTML:
            case IndexTaskInfo.JUMP_BROWSE:
            case IndexTaskInfo.JUMP_NEW_SPORTS_VALUE_1:
            case IndexTaskInfo.JUMP_NEW_SPORTS_VALUE:
                loadReadTask(activity, guideItem);
                break;
            case IndexTaskInfo.JUMP_NEW_RESULT:
                loadQuesResultAlready(activity, guideItem);// 结果页
                break;
            default:
                break;
        }
    }

    /**
     * 全面检查返回结果成功后跳转回首页
     *
     * @param activity
     * @param info
     */
    public void jumpToIndex(final BaseFragment activity, final GuideFullCheckInfo info) {
        new AsyncTask<Integer, Integer, Integer>() {

            @Override
            protected Integer doInBackground(Integer... params) {
                try {
                    ComveeHttp http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_GUIDE_TASK_FULL_CHECK_RESULT);
                    http.setPostValueForKey("taskCode", info.getTaskCode());
                    // http.setPostValueForKey("task_status",
                    // String.valueOf(info.getTaskStatus()));//new
                    http.setPostValueForKey("task_status", String.valueOf(info.getTaskStatus()));
                    String result = http.startSyncRequestString();
                    final ComveePacket packet = ComveePacket.fromJsonString(result);
                    if (packet.getResultCode() != 0) {
                        return 1;
                    }
                } catch (Exception e) {

                }
                return 2;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPreExecute();
                if (result == 2) {
                    try {
                        IndexFrag.toFragment(activity.getActivity(),true); 
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    /**
     * 第四阶段运动推荐完成后返回首页
     *
     * @param activity
     * @param info
     */
    public void jumpToIndexForSport(final BaseFragment activity, final IndexTaskInfo info) {
        new AsyncTask<Integer, Integer, Integer>() {

            @Override
            protected Integer doInBackground(Integer... params) {
                try {
                    ComveeHttp http = new ComveeHttp(activity.getActivity().getApplicationContext(), ConfigUrlMrg.NEW_GUIDE_TASK_FULL_CHECK_RESULT);
                    http.setPostValueForKey("taskCode", info.getTaskCode() + "");
                    // http.setPostValueForKey("task_status",
                    // String.valueOf(info.getTaskStatus()));//new
                    http.setPostValueForKey("task_status", String.valueOf(info.getStatus()));
                    String result = http.startSyncRequestString();
                    final ComveePacket packet = ComveePacket.fromJsonString(result);
                    if (packet.getResultCode() != 0) {
                        return 1;
                    }
                } catch (Exception e) {

                }
                return 2;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPreExecute();
                if (result == 2) {
                    try {
                        IndexFrag.toFragment(activity.getActivity(),true); 
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    public void jumpNextBrowseTask(final BaseFragment activity, IndexTaskInfo info) {
        info.setSeq(info.getSeq() + 1);
        loadReadTask(activity, info);
    }

    public void jumpPreBrowseTask(final BaseFragment activity, IndexTaskInfo info) {
        info.setSeq(info.getSeq() - 1);
        loadReadTask(activity, info);
    }

    // 判断是否是最后一页
    public boolean isBrowseLastPage(final BaseFragment activity, IndexTaskInfo info, GuideBrowseInfo nInfo) {
        if (info.getType() == 9) {
            return info.getTotal() == info.getSeq();
        }
        return info.getTotal() == nInfo.getSeq();
    }

    public boolean isBrowseFirstPage(final BaseFragment activity, IndexTaskInfo info, GuideBrowseInfo nInfo) {
        if (info.getType() == 9) {
            return info.getSeq() == 1;
        }
        return info.getSeq() == (nInfo.getMsgtype() == 0 ? 1 : 0);
    }
}
