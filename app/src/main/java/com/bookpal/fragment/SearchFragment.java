package com.bookpal.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bookpal.R;
import com.bookpal.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText mEditTextBookName, mEditTextLocation, mEditTextAuthorOrIsbn;
    private Button mButtonSearchBook;
    private LinearLayout mLinearLayoutMain;
    private ProgressBar mProgressBar;
    private DatabaseReference mDatabase;
    private Context mContext;
    private List<Book> mBookList;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        linkViewId(view);

        /*Intent intent = new Intent(getActivity(), Chat.class);
        intent.putExtra("username", SharedPreference.getString(getActivity(), AppConstants.USER_NAME));
        intent.putExtra("userid", SharedPreference.getString(getActivity(), AppConstants.USER_ID));
        getActivity().startActivity(intent);*/

        return view;
    }

    private void linkViewId(View view) {
        mEditTextBookName = (EditText) view.findViewById(R.id.edittext_book_name);
        mEditTextLocation = (EditText) view.findViewById(R.id.edittext_location);
        mEditTextAuthorOrIsbn = (EditText) view.findViewById(R.id.edittext_isbn);
        mLinearLayoutMain = (LinearLayout) view.findViewById(R.id.linear_layout_main);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mButtonSearchBook = (Button) view.findViewById(R.id.button_search);

        mBookList = new ArrayList<>();

        mButtonSearchBook.setOnClickListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                // Get a reference to our posts
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("/users/books");
                //Query queryRef = ref.orderByChild("books");

                // Attach a listener to read the data at our posts reference
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Book book = postSnapshot.getValue(Book.class);
                            mBookList.add(book);
                        }
                        Log.e("data", mBookList.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("read failed: " + databaseError.getCode());
                    }
                });

                break;
        }
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
