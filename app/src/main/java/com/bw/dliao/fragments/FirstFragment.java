package com.bw.dliao.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bw.dliao.activitys.TabActivity;
import com.bw.dliao.adapter.IndexAdapter;
import com.bw.dliao.R;
import com.bw.dliao.base.BaseMvpFragment;
import com.bw.dliao.base.IApplication;
import com.bw.dliao.bean.DataBean;
import com.bw.dliao.bean.IndexBean;
import com.bw.dliao.presenter.FirstFragmentPresenter;
import com.bw.dliao.view.FirstFragmentView;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.greendao.AbstractDaoMaster;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 *
 *  瀑布流 和列表 显示 距离 按照 墨迹天际 ui实现
 *  数据持久化到本地  采用 greendao
 *  服务器返回的数据 存储到本地数据库，第一次启动的时候， 先从本地数据库读取显示，服务器返回数据，显示最新数据
 *
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends BaseMvpFragment<FirstFragmentView, FirstFragmentPresenter> implements FirstFragmentView {


    private TabActivity activity ;
    IndexAdapter adapter;
    @BindView(R.id.pub_title_leftbtn)
    Button pubTitleLeftbtn;
    @BindView(R.id.pub_title_title)
    TextView pubTitleTitle;
    @BindView(R.id.pub_title_rightbtn)
    Button pubTitleRightbtn;
    @BindView(R.id.recycleview_indexfragment)
    RecyclerView recycleviewIndexfragment;
    @BindView(R.id.springview_indexfragment)
    SpringView springviewIndexfragment;
    Unbinder unbinder;
    private LinearLayoutManager linearLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private HorizontalDividerItemDecoration horizontalDividerItemDecoration;

    int page = 1 ;
    @Override
    public FirstFragmentPresenter initPresenter() {
        return new FirstFragmentPresenter();
    }

    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (TabActivity) getActivity() ;
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        unbinder = ButterKnife.bind(this, view);


        initView(view);

        return view;
    }

//    pubTitleRightbtn.tag
//     1 切换成 瀑布流 2 切换成 线性布局
    private void initView(View view) {

        pubTitleLeftbtn.setVisibility(View.GONE);
        pubTitleRightbtn.setVisibility(View.VISIBLE);
        pubTitleRightbtn.setTag(1);
        pubTitleRightbtn.setText("切换模式");

        pubTitleRightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int) v.getTag() ;
                if(tag == 1){
                    pubTitleRightbtn.setTag(2);
                    toStaggeredGridLayoutManager();


                } else {
                    pubTitleRightbtn.setTag(1);
                    toLinearLayoutManager();
                }
            }
        });

        horizontalDividerItemDecoration = new HorizontalDividerItemDecoration.Builder(getActivity()).build();
        adapter = new IndexAdapter(getActivity());
        toLinearLayoutManager();

        presenter.getData(page);


        springviewIndexfragment.setHeader(new DefaultHeader(getActivity()));
        springviewIndexfragment.setFooter(new DefaultFooter(getActivity()));

        springviewIndexfragment.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("onRefresh = " );
                page = 1 ;
                presenter.getData(page);
            }

            @Override
            public void onLoadmore() {
                System.out.println("onLoadmore = " );
                presenter.getData(++page);

            }
        });

    }

    public void toLinearLayoutManager(){
        if(linearLayoutManager == null){
            linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        adapter.dataChange(1);
        recycleviewIndexfragment.setLayoutManager(linearLayoutManager);
        recycleviewIndexfragment.setAdapter(adapter);
        recycleviewIndexfragment.addItemDecoration(horizontalDividerItemDecoration);

    }


    public void toStaggeredGridLayoutManager(){
        if(staggeredGridLayoutManager == null){
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        }
        adapter.dataChange(2);
        recycleviewIndexfragment.setLayoutManager(staggeredGridLayoutManager);
        recycleviewIndexfragment.setAdapter(adapter);
        recycleviewIndexfragment.removeItemDecoration(horizontalDividerItemDecoration);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void success(IndexBean indexBean,int page) {



        adapter.setData(indexBean,page);

    }

    @Override
    public void failed(int code,int page) {

    }
}
