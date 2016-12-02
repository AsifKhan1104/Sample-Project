package com.bookpal.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.bookpal.R;
import com.bookpal.database.DBAdapter;
import com.bookpal.model.AddBook;
import com.bookpal.utility.AlertDialogFragment;
import com.bookpal.utility.AppConstants;
import com.bookpal.utility.SharedPreference;
import com.bookpal.utility.Utility;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText mEditTextBookName, mEditTextAuthorName, mEditTextIsbn, mEditTextPublishingYear, mEditTextLanguage;
    private Button mButtonAddBook;
    private RadioGroup mRadioGroup;
    private LinearLayout mLinearLayoutMain;
    private ProgressBar mProgressBar;
    private DatabaseReference mDatabase;
    private Context mContext;
    private AutoCompleteTextView mAutoCompleteTextViewLocality;

    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        // Get firebase database instance to read / write data
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        linkViewId(view);
        constructAutoCompleteTextViewData();
        return view;
    }

    private void constructAutoCompleteTextViewData() {
        DBAdapter dbAdapter = new DBAdapter(mContext);
        dbAdapter.open();

        List<String> pincode = dbAdapter.GetPincodes();
        List<String> area = dbAdapter.GetArea();

        pincode.addAll(area);

        // close database
        dbAdapter.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (mContext, android.R.layout.select_dialog_item, pincode);
        mAutoCompleteTextViewLocality.setAdapter(adapter);
    }

    private void linkViewId(View view) {
        mEditTextBookName = (EditText) view.findViewById(R.id.editText_bookName);
        mEditTextAuthorName = (EditText) view.findViewById(R.id.editText_authorName);
        mEditTextIsbn = (EditText) view.findViewById(R.id.editText_isbn);
        mEditTextPublishingYear = (EditText) view.findViewById(R.id.editText_publishingYear);
        mEditTextLanguage = (EditText) view.findViewById(R.id.editText_language);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        mLinearLayoutMain = (LinearLayout) view.findViewById(R.id.linearLayout_Main);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mAutoCompleteTextViewLocality = (AutoCompleteTextView) view.findViewById(R.id.editText_locality);
        mButtonAddBook = (Button) view.findViewById(R.id.button_add);

        mButtonAddBook.setOnClickListener(this);
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
            case R.id.button_add:
                if (Utility.isNetworkConnected(mContext)) {
                    if (checkValidation()) {
                        showProgressBar();
                        addNewBook(SharedPreference.getString(mContext, AppConstants.USER_ID));
                    } else if (!(mEditTextBookName.getText().length() > 0)) {
                        Utility.showToastMessage(mContext, "Please enter Book Name to add the book");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mEditTextBookName);
                    } else if (!(mEditTextAuthorName.getText().length() > 0)) {
                        Utility.showToastMessage(mContext, "Please enter Author Name to add the book");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mEditTextAuthorName);
                    } else if (!(mEditTextPublishingYear.getText().length() > 0)) {
                        Utility.showToastMessage(mContext, "Please enter Publishing Year to add the book");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mEditTextPublishingYear);
                    } else if (!(mEditTextLanguage.getText().length() > 0)) {
                        Utility.showToastMessage(mContext, "Please enter Language to add the book");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mEditTextLanguage);
                    } else if (!(mAutoCompleteTextViewLocality.getText().length() > 0)) {
                        Utility.showToastMessage(mContext, "Please enter Locality to add the book");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mAutoCompleteTextViewLocality);
                    }
                } else {
                    Utility.showToastMessage(mContext, getResources().getString(R.string.no_internet_connection));
                }
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

    // add new book to user's firebase database
    private void addNewBook(String userId) {
        AddBook addBook = new AddBook();
        addBook.setBookName(mEditTextBookName.getText().toString().trim());
        addBook.setAuthorName(mEditTextAuthorName.getText().toString().trim());
        addBook.setIsbn(mEditTextIsbn.getText().toString().trim());
        addBook.setPublishingYear(mEditTextPublishingYear.getText().toString().trim());
        addBook.setLanguage(mEditTextLanguage.getText().toString().trim());
        addBook.setLocality(mAutoCompleteTextViewLocality.getText().toString().trim());
        if (mRadioGroup.getCheckedRadioButtonId() == R.id.radioButton_hardcover) {
            addBook.setBookType(AppConstants.TYPE_HARDCOVER);
        } else {
            addBook.setBookType(AppConstants.TYPE_PAPERBACK);
        }
        addBook.setUserId(userId);

        int bookCount = SharedPreference.getInt(mContext, AppConstants.PREF_KEY_BOOK_COUNT);
        mDatabase.child("users").child("books").child("book_" + String.valueOf(bookCount)).setValue(addBook);
        hideProgressBar();

        // increase book count by 1
        SharedPreference.setInt(mContext, AppConstants.PREF_KEY_BOOK_COUNT, bookCount + 1);

        // show success dialog
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.show(getFragmentManager(), getResources().getString(R.string.label_ok));

        // clear all edit texts for next book details
        clearAllEditTexts();
    }

    private void clearAllEditTexts() {
        mEditTextBookName.setText("");
        mEditTextAuthorName.setText("");
        mEditTextIsbn.setText("");
        mEditTextPublishingYear.setText("");
        mEditTextLanguage.setText("");
        mRadioGroup.check(R.id.radioButton_paperback);
    }

    private void showProgressBar() {
        mLinearLayoutMain.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mLinearLayoutMain.setVisibility(View.VISIBLE);
    }

    private boolean checkValidation() {
        if (mEditTextBookName.getText().length() > 0 &&
                mEditTextAuthorName.getText().length() > 0 &&
                mEditTextPublishingYear.getText().length() > 0 &&
                mEditTextLanguage.getText().length() > 0) {
            return true;
        }
        return false;
    }
}
