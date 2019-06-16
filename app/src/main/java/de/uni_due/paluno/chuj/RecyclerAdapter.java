package de.uni_due.paluno.chuj;

import android.content.Context;
import android.graphics.Picture;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uni_due.paluno.chuj.Models.Datum;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>
{
    private List<String> list;
    private Context mContext;
    private OnFriendListener mOnFriendListener;
   ;
    private static Map<String,List<Datum>> backupMap;
    private Date time;
    private SimpleDateFormat formatter;
    private String stringDatum;
    private List<Datum> temporaryList;
    ;


    public RecyclerAdapter(List<String> list, Context mContext, OnFriendListener onFriendListener)

    {

        backupMap=null;
        this.mContext=mContext;
        this.list=list;
        this.mOnFriendListener = onFriendListener;
    }
    public RecyclerAdapter(List<String> list, Context mContext, OnFriendListener onFriendListener,Map<String,List<Datum>> backMap)

    {

        this.mContext=mContext;
        this.list=list;
        this.mOnFriendListener = onFriendListener;
        this.backupMap=backMap;
        temporaryList=new ArrayList<Datum>();
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=  LayoutInflater.from(mContext).inflate(R.layout.text_view_layout,null);

        MyViewHolder myViewHolder = new MyViewHolder(view, mOnFriendListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String contact = list.get(position).toString();

        holder.firstLine.setText(contact);
        if(backupMap!=null) {

                int indexListInside = backupMap.get(contact).size() - 1;
                stringDatum = backupMap.get(contact).get(indexListInside).getDateTime();
                try {
                    time = formatter.parse(stringDatum);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                stringDatum = time.toString();
                int index = stringDatum.indexOf("MESZ");
                //if (stringDatum != NULL)
                stringDatum = stringDatum.substring(0, index + 1);
                String messageSender = backupMap.get(contact).get(indexListInside).getSender();
                if (messageSender.equals(contact)) {
                    if(isBase64(backupMap.get(contact).get(indexListInside).getData())){
                        holder.secondLine.setText(stringDatum + ", " + messageSender + ": Picture" );
                    }
                    else{
                        holder.secondLine.setText(stringDatum + ", " + messageSender + ": " + backupMap.get(contact).get(indexListInside).getData());
                    }
                } else {
                    if(isBase64(backupMap.get(contact).get(indexListInside).getData())){
                        holder.secondLine.setText(stringDatum + ":Picture " );
                    }
                    else{
                        holder.secondLine.setText(stringDatum + ": " + backupMap.get(contact).get(indexListInside).getData());
                    }
                }

            }



    }
    public static boolean isBase64(String str) {
        if (TextUtils.isEmpty(str))
            return false;
        try {
            Base64.decode(str, 12);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  // User Item, der bei Initialisierung erzeugt wird und der Platz für hält
    {


        OnFriendListener onFriendListener;
        TextView firstLine;
        TextView secondLine;
        public MyViewHolder(View itemView, OnFriendListener onFriendListener)
        {
            super (itemView);
            firstLine = (TextView) itemView.findViewById(R.id.firstLibe);
            secondLine = (TextView) itemView.findViewById(R.id.secondLine);
            this.onFriendListener = onFriendListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            onFriendListener.onNoteClick(getAdapterPosition());
        }

    }
    public interface OnFriendListener
    {
        void onNoteClick(int position);
    }
}
