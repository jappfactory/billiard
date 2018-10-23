package kr.co.pointn.golfya;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


public class DriverFragment extends Fragment implements AbsListView.OnScrollListener {

    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    public  ListView driverMovieListView;
    public List<DriverMovie> driverMovieList;
    public DriverMovieListAdapter driveradapter;
    private  ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수


    Activity activity;
    String target = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=2&key=AIzaSyBn4fOG4zKOYVbYtcMtGj8gGsVVpTYb68g&safeSearch=strict&type=video&q=드라이버+스윙+레슨&pageToken=";

    private OnFragmentInteractionListener mListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (Activity) getActivity();
    }
    public DriverFragment() {}

    public static DriverFragment newInstance() {
        DriverFragment fragment = new DriverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

       // progressBar.setVisibility(View.GONE);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle b) {
        super.onActivityCreated(b);

        driverMovieListView  = getView().findViewById(R.id.subDriverListView);
        driverMovieList = new ArrayList<DriverMovie>();
        driveradapter = new DriverMovieListAdapter(activity, driverMovieList, this);
        driverMovieListView.setAdapter(driveradapter);


        Log.d("driverMovieList", ""+driverMovieList);
        //driverMovieList.add(new DriverMovie("https://www.sacoop.kr/upload/project_img/33.jpg","쥬피터 아이언 영상","2018-10-10", "0"));
        //new LoadMovieTask(getActivity(), driverMovieList, driverMovieListView, driveradapter, target).execute();


        //Toast.makeText (getActivity(), "클릭a"  , Toast.LENGTH_LONG).show();

        progressBar.setVisibility(View.GONE);

        //Log.d("driverMovieList6", ""+driverMovieList);
        driverMovieListView.setOnScrollListener(this);

        // 다음 데이터를 불러온다.
        getItem(target);
    }
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {


        // 1. OnScrollListener.SCROLL_STATE_IDLE : 스크롤이 이동하지 않을때의 이벤트(즉 스크롤이 멈추었을때).
        // 2. lastItemVisibleFlag : 리스트뷰의 마지막 셀의 끝에 스크롤이 이동했을때.
        // 3. mLockListView == false : 데이터 리스트에 다음 데이터를 불러오는 작업이 끝났을때.
        // 1, 2, 3 모두가 true일때 다음 데이터를 불러온다.
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            // 화면이 바닦에 닿을때 처리
            // 로딩중을 알리는 프로그레스바를 보인다.
            progressBar.setVisibility(View.VISIBLE);

            String target = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=2&key=AIzaSyBn4fOG4zKOYVbYtcMtGj8gGsVVpTYb68g&safeSearch=strict&type=video&q=드라이버+스윙+레슨&pageToken=";


            String aa= SharedPreference.getSharedPreference(getActivity(), "nextPageToken");



          //Toast.makeText (getActivity(), "아래" + aa, Toast.LENGTH_LONG).show();

            try {

                //Context context = MainActivity.getAppContext();
               //mactivity = (MainActivity) context.getApplicationContext();
                target = target + aa;
                //Toast.makeText (getActivity(), "클릭2" + target , Toast.LENGTH_LONG).show();

            }catch (Exception e){

            }

            //Toast.makeText (getActivity(), "클릭" + MainActivity.pageToken , Toast.LENGTH_LONG).show();


            // 다음 데이터를 불러온다.
            getItem(target);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // firstVisibleItem : 화면에 보이는 첫번째 리스트의 아이템 번호.
        // visibleItemCount : 화면에 보이는 리스트 아이템의 갯수
        // totalItemCount : 리스트 전체의 총 갯수
        // 리스트의 갯수가 0개 이상이고, 화면에 보이는 맨 하단까지의 아이템 갯수가 총 갯수보다 크거나 같을때.. 즉 리스트의 끝일때. true
        lastItemVisibleFlag = true;
       // Toast.makeText (getActivity(), "위로" , Toast.LENGTH_LONG).show();
    }

    public void getItem(String target){

        // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 mLockListView 를 true로 설정한다.
        mLockListView = true;
        //Log.d("target", ""+target);

        new LoadMovieTask(getActivity(), driverMovieList, driverMovieListView, driveradapter, target).execute();

       // driverMovieListView.setAdapter(driveradapter);
        Log.d("driverMovieList6", ""+driverMovieList);

        // 1초 뒤 프로그레스바를 감추고 데이터를 갱신하고, 중복 로딩 체크하는 Lock을 했던 mLockListView변수를 풀어준다.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                driveradapter.notifyDataSetChanged();

               // driveradapter.setNotifyOnChange(false);
                progressBar.setVisibility(View.GONE);
                mLockListView = false;

 /*               int fVisible = driverMovieListView.getFirstVisiblePosition();
                View vFirst = driverMovieListView.getChildAt(0);
                int pos = 0;
                if (vFirst != null) pos = vFirst.getTop();

//Restore the position
                driverMovieListView.setSelectionFromTop(fVisible, pos);*/

            }
        },1000);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        //new LoadMovieTask(getContext(), driverMovieList).execute();

        View view=inflater.inflate(R.layout.fragment_driver, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        //progressBar.setVisibility(View.GONE);

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

       // new LoadMovieTask(getActivity(), driverMovieList, driverMovieListView, driveradapter, target).cancel(true);

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


