package cn.fjlcx.android.imitatejianshu.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fjlcx.android.imitatejianshu.R;
import cn.fjlcx.android.imitatejianshu.global.AppContext;
import cn.fjlcx.android.imitatejianshu.widget.ToolBarSet;
import cn.fjlcx.android.stateframelayout.StateAttr;
import cn.fjlcx.android.stateframelayout.StateFrameLayout;

/**
 * 基类的Fragment
 *
 * @author ling_cx
 * @date 2017/11/03.
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView {
	protected final String TAG = this.getClass().getSimpleName();
	@Nullable
	@BindView(R.id.sfl_state)
	StateFrameLayout mStateFrameLayout;

	@Inject
	protected P mPresenter;
	public Context mContext;
	public View view;
	private Toolbar mToolbar;
	private TextView mTvCenterTitle;
	private ToolBarSet mToolBarSet;
	public MaterialDialog progress;

	public Gson gson = new Gson();
	public AppContext app;
	protected boolean isVisible;
	private boolean isPrepared;
	private boolean isFirst = true;
	/**
	 * 登录超时标记
	 */
	private int loginTimeOutCode = 2;


	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.base_layout, container, false);
		mTvCenterTitle = (TextView) view.findViewById(R.id.tv_center_title);
		initDefaultView(attachLayoutRes(), view);
		init();
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		isPrepared = true;
		lazyLoad();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void initDefaultView(int layoutResId, View view) {
		FrameLayout container = (FrameLayout) view.findViewById(R.id.container);
		mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
		View childView = LayoutInflater.from(getActivity()).inflate(layoutResId, null);
		container.addView(childView, 0);
		ButterKnife.bind(this, container);
	}

	private void init() {
		mContext = getActivity();
		app = AppContext.getInstance();
		mToolBarSet = new ToolBarSet(mToolbar,mTvCenterTitle, (AppCompatActivity) getActivity());
		progress = new MaterialDialog.Builder(mContext)
				.content(getResources().getString(R.string.loading))
				.progress(true, 0)
				.cancelable(false)
				.build();
		initInject();
		initViews();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			lazyLoad();
		} else {
			isVisible = false;
		}
	}

	protected void lazyLoad() {
		if (!isPrepared || !isVisible || !isFirst) {
			return;
		}
		if (NetworkUtils.isAvailableByPing()) {
			Log.d(TAG, "initDataCounts: ");
			initData();
		} else {
			showNetError();
		}
		isFirst = false;
	}

	/**
	 * 绑定布局文件
	 *
	 * @return 布局文件ID
	 */
	@LayoutRes
	protected abstract int attachLayoutRes();

	/**
	 * 初始化视图控件
	 */
	protected abstract void initViews();

	/**
	 * 初始化数据
	 */
	protected abstract void initData();

	/**
	 * 初始化dagger2
	 */
	protected abstract void initInject();

	/**
	 * 获取所需的危险权限进行请求
	 * @return
	 */
	protected abstract String[] getNeedPermissions();


	@Override
	public void requestFail(String message) {
		Log.d(TAG, "requestFail: " + message);
		progress.dismiss();
		showErr(message);
	}

	@Override
	public void operateFail(int code, final String message) {
		progress.dismiss();
		ToastUtils.showShort(message);
	}

	/**
	 * 获取Toolbar对象
	 *
	 * @return
	 */
	public ToolBarSet getToolBar() {
		if (mToolBarSet == null) {
			mToolBarSet = new ToolBarSet(mToolbar,mTvCenterTitle, (AppCompatActivity) getActivity());
		}
		return mToolBarSet;
	}

	public int getBundleInt() {
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			return bundle.getInt("flag");
		} else {
			return -1;
		}
	}

	public String getBundleString() {
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			return bundle.getString("busikey");
		} else {
			return null;
		}
	}

	@Override
	public void showData() {
		if (mStateFrameLayout != null) {
			mStateFrameLayout.showState(new StateAttr.Builder()
					.setState(StateAttr.State.datas)
					.build()
			);
		}
	}

	@Override
	public void showLoding() {
		if (mStateFrameLayout != null) {
			mStateFrameLayout.showState(new StateAttr.Builder()
					.setState(StateAttr.State.loading)
					.setMessage("正在加载")
					.setMessageSize(14)
					.setMessageColor(R.color.state_text_color)
					.build()
			);
		}
	}

	@Override
	public void showNetError() {
		if (mStateFrameLayout != null) {
			mStateFrameLayout.showState(new StateAttr.Builder()
					.setState(StateAttr.State.offline)
					.setShowImage(true)
					.setImageRes(R.mipmap.ic_net_error)
					.setMessage(getResources().getString(R.string.state_offline))
					.setMessageSize(14)
					.setMessageColor(R.color.state_text_color)
					.build()
			);
		}
	}

	@Override
	public void showErr(String err) {
		if (mStateFrameLayout != null) {
			mStateFrameLayout.showState(new StateAttr.Builder()
					.setState(StateAttr.State.error)
					.setShowImage(true)
					.setImageRes(R.mipmap.ic_error)
					.setMessage(err)
					.setMessageSize(14)
					.setMessageColor(R.color.state_text_color)
					.build()
			);
		}
	}

	@Override
	public void showNonData() {
		if (mStateFrameLayout != null) {
			mStateFrameLayout.showState(new StateAttr.Builder()
					.setState(StateAttr.State.empty)
					.setShowImage(true)
					.setImageRes(R.mipmap.ic_empty)
					.setMessage("暂无数据")
					.setMessageSize(14)
					.setMessageColor(R.color.state_text_color)
					.build()
			);
		}
	}

}
