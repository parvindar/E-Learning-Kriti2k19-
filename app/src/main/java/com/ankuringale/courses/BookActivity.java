package com.ankuringale.courses;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        FirebaseApp.initializeApp(this);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public static ListView clublistview;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragement_club_dep, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            clublistview = rootView.findViewById(R.id.lv_list);

            ArrayList<String> clublist = new ArrayList<>();
            clublist.add("Aeromodelling");
            clublist.add("Astronomy");
            clublist.add("Coding");
            clublist.add("Consulting and Analytics");
            clublist.add("Electronics");
            clublist.add("Prakriti");
            clublist.add("Finance and Economics");
            clublist.add("Robotics");
            clublist.add("Science and Quiz");
            clublist.add("TechEvince");
            clublist.add("Green Automobile");
            clublist.add("Entrepreneurial Development Cell");
            clublist.add("Udgam");

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, clublist);
            clublistview.setAdapter(arrayAdapter);


            clublistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getContext(),BookListActivity.class);
                    intent.putExtra("club",clublistview.getItemAtPosition(position).toString());
                    startActivity(intent);

                }
            });

            return rootView;
        }
    }


    public static class PlaceholderFragment2 extends Fragment {
        public static ListView deplistview;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment2() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment2 newInstance(int sectionNumber) {
            PlaceholderFragment2 fragment = new PlaceholderFragment2();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragement_club_dep, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            deplistview = rootView.findViewById(R.id.lv_list);

            ArrayList<String> department = new ArrayList<>();

            ArrayList<String> branchlist = new ArrayList<String>();
            branchlist.add("CSE");
            branchlist.add("ECE");
            branchlist.add("ME");
            branchlist.add("CE");
            branchlist.add("DD");
            branchlist.add("BSBE");
            branchlist.add("CL");
            branchlist.add("EEE");
            branchlist.add("CST");
            branchlist.add("MC");
            branchlist.add("EPH");
            branchlist.add("HSS");



            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, branchlist);

            deplistview.setAdapter(arrayAdapter);


            deplistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getContext(),BookListActivity.class);
                    intent.putExtra("club",deplistview.getItemAtPosition(position).toString());
                    startActivity(intent);

                }
            });

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0)
            {
                return  PlaceholderFragment.newInstance(position);
            }
            if(position==1)
            {
                return  PlaceholderFragment2.newInstance(position);
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:
                    return "Club";
                case 1:
                    return "Department";

                default:
                    return null;
            }
        }
    }




}
