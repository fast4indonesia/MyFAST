package motiolabs.myfast.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import motiolabs.myfast.R;

public class HomeFragment extends Fragment {

    private Context mContext;
    public View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.mContext = getActivity().getApplicationContext();
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_myprofile);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //((HomeActivity) getActivity()).setFragmentPosition(HomeActivity.FragPOS.HOME);
        super.onActivityCreated(savedInstanceState);
    }

}
