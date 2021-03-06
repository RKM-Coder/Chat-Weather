package eu.siacs.conversations.demo.adapter;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import eu.siacs.conversations.demo.FirstFragment;
import eu.siacs.conversations.demo.SecondFragment;
import eu.siacs.conversations.demo.WeatherFragment;


public class CustomViewPagerAdapter extends FragmentStatePagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Chats", "Weather" };
    private Context context;

    private final FragmentManager fragmentManager;
    public CustomViewPagerAdapter(FragmentManager fm){
        super(fm);
        fragmentManager=fm;
       /* fragments=new Fragment[]{
                new ConversationFragment(),
                new ConversationFragment()
        };*/
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.e("FRRRRR",""+position);
        switch (position) {
            case 0:
                return FirstFragment.newInstance();
            case 1:
                return WeatherFragment.newInstance();

        }
        return null;
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
