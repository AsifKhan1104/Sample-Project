package com.bookpal.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bookpal.R;
import com.bookpal.adapter.RecyclerViewAdapter_MyBooks;
import com.bookpal.model.Book;
import com.bookpal.utility.AppConstants;
import com.bookpal.utility.SharedPreference;
import com.bookpal.utility.Utility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyBooksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBooksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LinearLayoutManager lLayout;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter_MyBooks mRecyclerViewAdapter;
    private ProgressBar mProgressBar;
    private TextView mTextViewNoData;
    private DatabaseReference mDatabaseReference;
    private Context mContext;

    public MyBooksFragment() {
        // Required empty public constructor
    }

    public static MyBooksFragment newInstance() {
        MyBooksFragment fragment = new MyBooksFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_books, container, false);
        linkViewId(view);
        return view;
    }

    private void linkViewId(View view) {
        mContext = getActivity();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        lLayout = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_my_books);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mTextViewNoData = (TextView) view.findViewById(R.id.textView_noData);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(lLayout);

        if (Utility.isNetworkConnected(mContext)) {
            fetchBookListFromServer();
        } else {
            mTextViewNoData.setText(R.string.no_internet_connection);
            mTextViewNoData.setVisibility(View.VISIBLE);
        }
    }

    private void fetchBookListFromServer() {
        String uid = SharedPreference.getString(mContext, AppConstants.USER_ID);
        showProgressBar();
        mDatabaseReference.child("users").child("books").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressBar();

                List<Book> list = new ArrayList<Book>();
                HashMap<String, HashMap<String, String>> hashMap = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                if (hashMap != null && hashMap.size() > 0) {
                    Iterator entries = hashMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Book addBook = new Book();
                        Map.Entry entry = (Map.Entry) entries.next();
                        HashMap<String, String> hashMap_sub = (HashMap<String, String>) entry.getValue();

                        Iterator iterator = hashMap_sub.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry_sub = (Map.Entry) iterator.next();
                            switch ((String) entry_sub.getKey()) {
                                case AppConstants.BOOK_NAME:
                                    addBook.setBookName(entry_sub.getValue().toString());
                                    break;
                                case AppConstants.AUTHOR_NAME:
                                    addBook.setAuthorName(entry_sub.getValue().toString());
                                    break;
                                case AppConstants.ISBN:
                                    if (entry_sub.getValue().toString().equals("")) {
                                        addBook.setIsbn("-");
                                    } else {
                                        addBook.setIsbn(entry_sub.getValue().toString());
                                    }
                                    break;
                                case AppConstants.PUBLISHING_YEAR:
                                    addBook.setPublishingYear(entry_sub.getValue().toString());
                                    break;
                                case AppConstants.LANGUAGE:
                                    addBook.setLanguage(entry_sub.getValue().toString());
                                    break;
                                case AppConstants.BOOK_TYPE:
                                    addBook.setBookType(entry_sub.getValue().toString());
                                    break;
                            }
                        }
                        // add this object to list
                        list.add(addBook);
                    }

                    // hide no data text view once data comes
                    if (list.size() > 0) {
                        mTextViewNoData.setVisibility(View.GONE);
                    }

                    mRecyclerViewAdapter = new RecyclerViewAdapter_MyBooks(mContext, list);
                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                } else {
                    mTextViewNoData.setText(R.string.no_book_added);
                    mTextViewNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressBar();
                mTextViewNoData.setText(R.string.error_msg);
                mTextViewNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void showProgressBar() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
