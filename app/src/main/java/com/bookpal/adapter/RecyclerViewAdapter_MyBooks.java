package com.bookpal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bookpal.R;
import com.bookpal.model.Book;

import java.util.List;

/**
 * Created by asif on 3/7/16.
 */
public class RecyclerViewAdapter_MyBooks extends RecyclerView.Adapter<RecyclerViewHolder_MyBooks> {
    private Context mContext;
    private List<Book> addedBooksList;

    public RecyclerViewAdapter_MyBooks(Context context, List<Book> list) {
        this.mContext = context;
        this.addedBooksList = list;
    }

    @Override
    public RecyclerViewHolder_MyBooks onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_my_books, parent, false);
        RecyclerViewHolder_MyBooks rcv = new RecyclerViewHolder_MyBooks(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder_MyBooks holder, final int position) {
        holder.mTextViewBookName.setText(addedBooksList.get(position).getBookName());
        holder.mTextViewAuthorName.setText("by " + addedBooksList.get(position).getAuthorName());
        //holder.mTextViewIsbn.setText(addedBooksList.get(position).getIsbn());
        holder.mTextViewPublishingYear.setText("publication year: " + addedBooksList.get(position).getPublishingYear());
        //holder.mTextViewLanguage.setText(addedBooksList.get(position).getLanguage());
        //holder.mTextViewBookType.setText(addedBooksList.get(position).getBookType());
    }

    @Override
    public int getItemCount() {
        return addedBooksList.size();
    }
}

class RecyclerViewHolder_MyBooks extends RecyclerView.ViewHolder {

    public final TextView mTextViewBookName, mTextViewAuthorName, mTextViewIsbn,
            mTextViewPublishingYear, mTextViewLanguage, mTextViewBookType;

    public RecyclerViewHolder_MyBooks(View itemView) {
        super(itemView);
        mTextViewBookName = (TextView) itemView.findViewById(R.id.textView_bookName);
        mTextViewAuthorName = (TextView) itemView.findViewById(R.id.textView_authorName);
        mTextViewIsbn = (TextView) itemView.findViewById(R.id.textView_isbn);
        mTextViewPublishingYear = (TextView) itemView.findViewById(R.id.textView_publishingYear);
        mTextViewLanguage = (TextView) itemView.findViewById(R.id.textView_language);
        mTextViewBookType = (TextView) itemView.findViewById(R.id.textView_bookType);
    }
}

