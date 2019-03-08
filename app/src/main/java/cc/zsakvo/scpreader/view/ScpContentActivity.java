package cc.zsakvo.scpreader.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cc.zsakvo.scpreader.R;
import cc.zsakvo.scpreader.adapter.ScpContentAdapter;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ScpContentActivity extends BaseActivity implements ScpContentAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScpContentAdapter adapter;
    private List<String> scpStr = new ArrayList<>();
    private List<String> scpDesc = new ArrayList<>();
    private List<Integer> noneList = new ArrayList<>();
    private String urla;

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_scp_content;
    }

    @Override
    public int bindMenu() {
        return 0;
    }

    @Override
    public void clickMenu(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void initView(View view) {
        toolbar = $(R.id.toolbar);
        recyclerView = $(R.id.scp_content_recy);
        swipeRefreshLayout = $(R.id.scp_content_srf);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.swipe));
        adapter = new ScpContentAdapter(scpStr, scpDesc,noneList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadContnt(urla);
            }
        });
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        swipeRefreshLayout.setRefreshing(true);
        String title = getIntent().getStringExtra("title");
        String urla = getIntent().getStringExtra("url");
        this.urla = urla;
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        loadContnt(urla);
    }

    private void loadContnt(String urla) {

        final String hostUrl = "http://scp-wiki-cn.wikidot.com/printer--friendly";
        String url = hostUrl+urla;

        scpStr.clear();
        scpDesc.clear();
        adapter.notifyDataSetChanged();

        JSONObject textObj = new JSONObject(true); //存储普通正文
        JSONObject picObj = new JSONObject(true);  //存储插图
        JSONObject footObj = new JSONObject(true);  //存储脚注

        Observable.create((ObservableOnSubscribe<Void>) emitter -> {

            try {
                Document doc = Jsoup.connect(url).get();
                //选择正文节点
                Element element_content = doc.selectFirst("#page-content > div");

                //判断是否需要授权
                if (element_content.text().contains("请输入安保证号")){
                    element_content = doc.selectFirst("div.collapsible-block-content");
                }

                //移除评分条目
                element_content.select("div[style=text-align:right]").remove();

                //移除底部翻页
                element_content.select("div.footer-wikiwalk-nav").remove();

                //抽出插图
                Elements elements_pic = element_content.select("div.scp-image-block");

                for (Element element:elements_pic){
                    String picUrl = element.select("img").attr("src");
                    String picDesc = element.select("div.scp-image-caption").text();
                    picObj.put(picDesc,picUrl);
                }

                element_content.select("div.scp-image-block").remove();

                //重新定位内容
                element_content = element_content.select("p:contains(SCP-)").first().parent();


                StringBuilder descTmp = new StringBuilder();
                String strTmp = "";

                for (Element element : element_content.children()) {
                    if (element.className().contains("scp-image-block")){
                        String picUrl = element.select("img").attr("src");
                        String picDesc = element.select("div.scp-image-caption").text();
                        picObj.put(picDesc,picUrl);
                    } else if(element.className().contains("footnotes-footer")){
                        StringBuilder footsb = new StringBuilder();
                        for (Element e:element.select("div.footnote-footer")){
                            footsb.append(e.text()).append("\n\n");
                        }
                        footObj.put("注释：",footsb.toString());
                    }else if(element.tagName().contains("blockquote")){
                        String title = element.selectFirst("p").text();
                        element.selectFirst("p").remove();
                        for (Element p:element.select("p")){
                            descTmp.append(p.text()).append("\n\n");
                        }
                        textObj.put(title,descTmp.toString().trim());
                    }
                    else{
                        if (element.getElementsByTag("strong").size() != 0) {
                            String titleStr = element.getElementsByTag("strong").text();
                            if (textObj.containsKey(titleStr)) titleStr+=" ";
                            element.select("strong").remove();
                            String descStr = element.text().trim();
                            descTmp = new StringBuilder(descStr);
                            strTmp = titleStr;
                            if (!StringUtil.isBlank(descStr)) {
                                textObj.put(titleStr, descStr);
                            } else {
                                textObj.put(titleStr, " ");
                            }
                        }else {
                            element.select("br").append("\\n");
                            String text = element.text().trim().replace("\\n","\n");
                            if (StringUtil.isBlank(descTmp.toString())){
                                descTmp.append(text);
                            }else {
                                descTmp.append("\n\n").append(text);
                            }
                            textObj.put(strTmp,descTmp);
                        }
                    }
                }

                textObj.putAll(footObj);

            }catch (Exception e){
                e.printStackTrace();
            }

            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "subscribe");
                    }

                    @Override
                    public void onNext(Void v) {
                        Log.d(TAG, "onNext: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "error:" + e);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                        int i = 0;
                        for (String key:textObj.keySet()){
                            if (StringUtil.isBlank(key)) continue;
                            scpStr.add(key);
                            if (StringUtil.isBlank(textObj.getString(key).trim())) noneList.add(i);
                            scpDesc.add(textObj.getString(key));
                            i++;
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });


    }

    @Override
    public void doOnStart() {

    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
