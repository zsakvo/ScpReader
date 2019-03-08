package cc.zsakvo.scpreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cc.zsakvo.scpreader.R;
import cc.zsakvo.scpreader.adapter.ScpSeriesAdapter;
import cc.zsakvo.scpreader.listener.ScpSeriesUrlListener;
import cc.zsakvo.scpreader.view.MainActivity;
import cc.zsakvo.scpreader.view.ScpContentActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

public class ScpSeriesFragment extends Fragment implements ScpSeriesUrlListener, ScpSeriesAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String url;
    private ScpSeriesAdapter adapter;
    private List<String> scpSn = new ArrayList<>();
    private List<String> scpContentUrl = new ArrayList<>();



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scp_series, container, false);
        recyclerView = root.findViewById(R.id.scp_series_recy);
        swipeRefreshLayout = root.findViewById(R.id.scp_series_srf);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.swipe));
        init();
        return root;
    }

    private void init(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScpSeriesAdapter(scpSn,scpContentUrl);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadScpSeries(url);
            }
        });
    }

    private void loadScpSeries(String url){

        if (swipeRefreshLayout!=null){
            swipeRefreshLayout.setRefreshing(true);
        }

        if (scpSn.size()!=0) {
            scpSn.clear();
            scpContentUrl.clear();
            adapter.notifyDataSetChanged();
        }

        Observable.create((ObservableOnSubscribe<Void>) emitter -> {
            Document doc = Jsoup.connect(url).get();
            Elements elements_ul = doc.getElementById("page-content").select("ul");
            int i = 0;
            for (Element ul:elements_ul){
                if (i>=1&&i<=10){
                    Elements elements_li = ul.select("li");
                    for (Element li:elements_li){
                        scpSn.add(li.text());
                        scpContentUrl.add(li.select("a").attr("href"));
                    }
                }
                i++;
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
                        Log.d(TAG, "error:"+e);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

    }

    @Override
    public void setUrl(String url) {
        this.url = url;
        loadScpSeries(url);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getContext(), ScpContentActivity.class);
        intent.putExtra("url",scpContentUrl.get(position));
        intent.putExtra("title",scpSn.get(position));
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", url);
    }

}
