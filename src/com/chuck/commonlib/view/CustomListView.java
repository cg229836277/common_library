package com.chuck.commonlib.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.chuck.commonlib.R;

public class CustomListView extends ListView implements OnScrollListener{
	
	private LayoutInflater inflater;
	private View headView;
	private View footView;
	
	private ImageView headerImageView;
	private ProgressBar headerProgressBar;
	private TextView headerTextView;
	private TextView headShowTimeView;
	
	private ImageView footImageView;
	private ProgressBar footProgressBar;
	private TextView footTextView;
	//private Button footButton;
	private DisplayMetrics localDisplayMetrics;
	
	private float tempY = 0f;
	
	private String lastTime;
	
	private SimpleDateFormat formatter;
	
	private int headerViewHeight = 0, footViewHeight = 0;
	private int headerViewMaxHeight = 0 , footViewMaxHeight = 0;
	
	private boolean isRefresh = false , isLoadMore = false;
	private boolean isAtHead = false;
	private boolean isAtBottom = false;
	private boolean mIsNeedLoadMore = false;
	
	private float absoluteDealt = 0;//滑动距离的绝对值
	private float dealt = 0;//滑动的相对值
	
	private int screenDensity = 0;
	
	private OnRefreshLoadListenner mListenner;
	
	private final String TAG = "chuck";
	
	private RotateAnimation imageRotate;
	
	private final static int HEAD_REFRESH_INIT = 0;
	private final static int HEAD_REFRESH_PREPARE = 1;
	private final static int HEAD_REFRESH_REFRESHING = 2;
	private final static int HEAD_REFRESH_END = 3;	
	
	private final static int FOOT_REFRESH_INIT = 0;
	private final static int FOOT_REFRESH_PREPARE = 1;
	private final static int FOOT_REFRESH_REFRESHING = 2;
	private final static int FOOT_REFRESH_END = 3;
	
	public static interface OnRefreshLoadListenner{
		public void startRefresh();
		public void startOnLoadMore();
	}
	
	public void setNeedLoadMore(boolean isNeedLoadMore){
		mIsNeedLoadMore = isNeedLoadMore;
	}
	
	public void setOnRefreshLoadListenner(OnRefreshLoadListenner listenner){
		mListenner = listenner;
	}
	
	public CustomListView(Context context){
		super(context);
	}

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);		
		inflater = LayoutInflater.from(context);	
		try{
			init(context);
		}catch(Exception e){
			Log.e("chuck", "msg" + e.getMessage());
		}
	}
	
	private void init(Context context){
		addHeaderView();
		addFooterView();
		
		setHeaderViewSize(context);
				
		getHeaderAndFootViewSize();
		
		setHeaderImageAnimation();
		
		setOnScrollListener(this);
		headView.setPadding(0, -1 * headerViewHeight, 0, 0);
		footView.setPadding(0, -1 * footViewHeight, 0, 0);
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	private void addHeaderView(){
		headView = inflater.inflate(R.layout.customlist_header_view, null);		
		headerImageView = (ImageView)headView.findViewById(R.id.head_arrow_image);
		headerImageView.setTag(true);
		headerProgressBar = (ProgressBar)headView.findViewById(R.id.head_show_progress_view);
		headerTextView = (TextView)headView.findViewById(R.id.head_show_text);
		headShowTimeView = (TextView)headView.findViewById(R.id.show_refresh_time);		
		addHeaderView(headView);
	}
	
	private void addFooterView(){
		footView = inflater.inflate(R.layout.customlist_footer_view, null);
		footImageView = (ImageView)footView.findViewById(R.id.foot_arrow_image);
		footImageView.setTag(true);
		footProgressBar = (ProgressBar)footView.findViewById(R.id.foot_show_progress_view);
		footTextView = (TextView)footView.findViewById(R.id.foot_show_text);
		//footButton = (Button)footView.findViewById(R.id.click_to_load_more);
		addFooterView(footView);
	}
	
	private void getHeaderAndFootViewSize(){
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		
		headView.measure(w, h);
		headerViewHeight = headView.getMeasuredHeight();
		
		footView.measure(w, h);
		footViewHeight = footView.getMeasuredHeight();
	}
	
	private void setHeaderViewSize(Context context){
		localDisplayMetrics = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		if(localDisplayMetrics != null){
			headerViewMaxHeight = localDisplayMetrics.heightPixels / 2;
			footViewMaxHeight = headerViewMaxHeight;
			screenDensity = (int)localDisplayMetrics.density;
		}
	}

	private void setHeaderImageAnimation(){
		imageRotate = new RotateAnimation(0, -180 , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		Interpolator interpolator = new LinearInterpolator();
		imageRotate.setInterpolator(interpolator);
		imageRotate.setFillAfter(true);
		imageRotate.setDuration(250);
	}
	
	private boolean isListennerNull(){
		if(mListenner != null){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();		
		switch (action) {
		case MotionEvent.ACTION_UP:	
			//Log.e(TAG, "开始下拉刷新:" + absoluteDealt + "dealt: " + dealt);
			if(dealt > 0 && absoluteDealt >= headerViewHeight && isAtHead){
				isRefresh = true;
				setHeaderViewState(HEAD_REFRESH_REFRESHING);
				startRefresh();
			}else if(mIsNeedLoadMore && dealt <= 0 && absoluteDealt >= footViewHeight && isAtBottom){
				isLoadMore = true;
				setFootViewState(FOOT_REFRESH_REFRESHING);
				startLoadMore();
			}else{
				setHeaderViewState(HEAD_REFRESH_END);
				setFootViewState(FOOT_REFRESH_END);
			}
			absoluteDealt = 0;
			dealt = 0;
			tempY = 0;
			break;
		case MotionEvent.ACTION_DOWN:
			tempY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			float currentY = ev.getRawY();
			dealt = currentY - tempY;
			absoluteDealt = Math.abs(dealt);
			//Log.e(TAG, "是否正在刷新：" + isRefresh);
			if(!isLoadMore && !isRefresh && isAtHead && dealt > 0){	
				setRefreshState();
			}else if(mIsNeedLoadMore && !isLoadMore && !isRefresh && isAtBottom && dealt < 0){
				setLoadMoreState();
			}
			break;
		default:
			break;
		}	
		return super.onTouchEvent(ev);
	}
	
	private void setRefreshState(){
		int currentAbsoluteDealt = (int)absoluteDealt;
		if(currentAbsoluteDealt <= headerViewMaxHeight){
			setHeaderViewHeight(currentAbsoluteDealt);
			if(currentAbsoluteDealt < headerViewHeight){
				setHeaderViewState(HEAD_REFRESH_INIT);
			}else if(currentAbsoluteDealt >= headerViewHeight){
				setHeaderViewState(HEAD_REFRESH_PREPARE);
			}
		}
	}
	
	private void startRefresh(){				
		if(!isListennerNull()){
			mListenner.startRefresh();
			setHeaderViewHeight(headerViewHeight);
		}
	}
	
	private void setLoadMoreState(){
		int currentAbsoluteDealt = (int)absoluteDealt;
		if(currentAbsoluteDealt <= footViewMaxHeight){
			setFootViewHeight(currentAbsoluteDealt);
			if(currentAbsoluteDealt < footViewMaxHeight){
				setFootViewState(FOOT_REFRESH_INIT);
			}else if(currentAbsoluteDealt >= footViewHeight){
				setFootViewState(FOOT_REFRESH_PREPARE);
			}
		}
	}
	
	private void startLoadMore(){
		if(!isListennerNull()){
			mListenner.startOnLoadMore();
			setHeaderViewHeight(footViewHeight);
		}
	}
	
	public void refreshComplete(){
		isRefresh = false;
		setHeaderViewHeight(0);
	}
	
	public void loadMoreComplete(){
		isLoadMore = false;
		setFootViewHeight(0);
	}
	
	private void setHeaderViewHeight(int headerViewHeight){
		ViewGroup.LayoutParams params = headView.getLayoutParams();
		params.height = headerViewHeight;
		headView.setLayoutParams(params);
	}
	
	private void setFootViewHeight(int footViewHeight){
		ViewGroup.LayoutParams params = footView.getLayoutParams();
		if(mIsNeedLoadMore && params != null){
			params.height = footViewHeight;
			footView.setLayoutParams(params);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		Log.e(TAG, "当前位置firstVisibleItem：" + firstVisibleItem);
		Log.e(TAG, "当前位置visibleItemCount：" + visibleItemCount);
		Log.e(TAG, "当前位置totalItemCount：" + totalItemCount);
		if(firstVisibleItem == 0){
			isAtHead = true;
			isAtBottom = false;
		}else{
			isAtHead = false;
		}
		
		if(firstVisibleItem + visibleItemCount == totalItemCount){
			isAtBottom = true;
			isAtHead = false;
		}else{
			isAtBottom = false;
		}		
	}
	
	private void setHeaderViewState(int state){
		switch (state) {
		case HEAD_REFRESH_INIT:
			headerImageView.setVisibility(View.VISIBLE);
			headShowTimeView.setVisibility(View.GONE);
			headerTextView.setText("下拉刷新");
			break;
		case HEAD_REFRESH_PREPARE:
			headerTextView.setText("松开立即刷新");
			headerImageView.setVisibility(View.VISIBLE);
			headShowTimeView.setVisibility(View.VISIBLE);
			headerProgressBar.setVisibility(View.GONE);		
			if((Boolean)(headerImageView.getTag())){
				headerImageView.clearAnimation();
				headerImageView.startAnimation(imageRotate);
				String currentDate = formatter.format(new Date());
				if(TextUtils.isEmpty(lastTime)){
					headShowTimeView.setText("刷新时间是：" + currentDate);
				}else{
					headShowTimeView.setText("上次刷新时间是：" + lastTime);
				}
				lastTime = currentDate;
			}
			headerImageView.setTag(false);
			break;
		case HEAD_REFRESH_REFRESHING:
			headerTextView.setText("正在刷新");
			headerImageView.clearAnimation();
			headerProgressBar.setVisibility(View.VISIBLE);
			headShowTimeView.setVisibility(View.GONE);
			headerImageView.setVisibility(View.GONE);			
			break;
		case HEAD_REFRESH_END:
			headerImageView.setTag(true);
			headerImageView.clearAnimation();
			refreshComplete();
			headerProgressBar.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	
	private void setFootViewState(int state){
		switch (state) {
		case FOOT_REFRESH_INIT:
			footImageView.setVisibility(View.VISIBLE);
			footTextView.setText("上拉加载");
			break;
		case FOOT_REFRESH_PREPARE:
			footTextView.setText("松开立即加载");
			footImageView.setVisibility(View.VISIBLE);
			footProgressBar.setVisibility(View.GONE);		
			if((Boolean)(footImageView.getTag())){
				footImageView.clearAnimation();
				footImageView.startAnimation(imageRotate);
			}
			footImageView.setTag(false);
			break;
		case FOOT_REFRESH_REFRESHING:
			footTextView.setText("正在加载");
			footImageView.clearAnimation();
			footProgressBar.setVisibility(View.VISIBLE);
			footImageView.setVisibility(View.GONE);			
			break;
		case FOOT_REFRESH_END:
			footImageView.setTag(true);
			footImageView.clearAnimation();
			loadMoreComplete();
			footProgressBar.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
}
