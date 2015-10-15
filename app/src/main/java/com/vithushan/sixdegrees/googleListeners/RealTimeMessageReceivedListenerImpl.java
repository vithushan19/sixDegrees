package com.vithushan.sixdegrees.googleListeners;

import android.app.Fragment;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.fragment.GameOverFragment;
import com.vithushan.sixdegrees.fragment.MainGameFragment;
import com.vithushan.sixdegrees.fragment.SelectActorFragment;
import com.vithushan.sixdegrees.util.NavigationUtils;
import com.vithushan.sixdegrees.util.SixDegreesUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by vnama on 10/10/2015.
 */
public class RealTimeMessageReceivedListenerImpl implements RealTimeMessageReceivedListener {

    private GameActivity mActivity;

    public RealTimeMessageReceivedListenerImpl (GameActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();


        if (buf[0] == 'W') { //Win game
            // handle end game broadcast
            MainGameFragment frag = (MainGameFragment) mActivity.getFragmentManager().findFragmentById(R.id.fragment_container);
            if (frag instanceof MainGameFragment) {

                ArrayList<String> historyIds = new ArrayList<>();

                for (int i=1; i<buf.length;) {
                    byte[] subArr = Arrays.copyOfRange(buf, i, i + 4);
                    int num = SixDegreesUtils.byteArrayToInt(subArr);
                    historyIds.add(String.valueOf(num));
                    i = i+4;
                }

                String [] historyIdsArr = new String[historyIds.size()];
                historyIds.toArray(historyIdsArr);
                NavigationUtils.gotoGameOverFragment(mActivity,false, historyIdsArr);
            }
        } else if (buf[0] == 'R') { //Rematch Request
            // handle end game broadcast
            mActivity.askForRematch();
        } else if (buf[0] == 'A') { //Rematch Request Accepted
            NavigationUtils.gotoSelectActorFragment(mActivity);
        } else if (buf[0] == 'D') { //Rematch Request Declined
            // handle end game broadcast
            Fragment frag = mActivity.getFragmentManager().findFragmentById(R.id.fragment_container);
            if (frag instanceof GameOverFragment) {
                ((GameOverFragment)frag).showRematchDeclined();
            }
        } else if (buf.length == 4) { //Actor message
            int oppSelectedActorId = SixDegreesUtils.byteArrayToInt(buf);
            SelectActorFragment frag = (SelectActorFragment) mActivity.getFragmentManager().findFragmentById(R.id.fragment_container);
            if (frag != null) {
                frag.setOppSelectedActor(oppSelectedActorId);
                frag.onSet();
            }
        }
    }

}
