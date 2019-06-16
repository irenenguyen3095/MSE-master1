package de.uni_due.paluno.chuj;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.uni_due.paluno.chuj.Models.Datum;

public class GetMessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_MAP=3;
    private static final int VIEW_TYPE_PICTURE_SENT =4;
    private static final int VIEW_TYPE_PICTURE_RECEIVED =5;
    private List<Datum> list;
    private Context mContext;

    private String senderName;
    private String recipent;
    private String sender;
    private Date time;
    private SimpleDateFormat formatter;
    private String stringDatum;
    private String lataitude;
    private String longtitude;
    private OnFriendListener mOnFriendListener;


    public GetMessageAdapter(List<Datum> list, Context mContext, String senderName, OnFriendListener onFriendListener) {

        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.mContext = mContext;
        this.list = list;
        this.senderName = senderName;
        this.mOnFriendListener = onFriendListener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_message_holder, parent, false);
            return new MyViewHolder(view,mOnFriendListener);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.their_message, parent, false);
            return new TheirViewHolder(view, mOnFriendListener);
        }else if(viewType == VIEW_TYPE_MESSAGE_MAP)
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.map_holder_layout, parent, false);
            return new TheirViewHolder(view, mOnFriendListener);
        }
        else if(viewType == VIEW_TYPE_PICTURE_RECEIVED){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_recived_holder_layout,parent,false);
            return new TheirPictureViewHolder(view, mOnFriendListener);
        }
        else if(viewType == VIEW_TYPE_PICTURE_SENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_sent_holder_layout,parent,false);
            return new myPictureViewHolder(view, mOnFriendListener);
        }
        else
        {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


        String message = list.get(i).getData();
        sender= list.get(i).getSender();
        stringDatum = list.get(i).getDateTime();
        try {
            time = formatter.parse(stringDatum);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        stringDatum = time.toString();
        int index = stringDatum.indexOf("T");
        stringDatum = stringDatum.substring(0, index+1);
        recipent = list.get(i).getRecipient();

        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                if(lataitude!=null&&longtitude!=null)
                {
                    ((MyViewHolder) viewHolder).myMessage.setText("Hey, "+recipent+" it`s my current location, please click on");
                    ((MyViewHolder) viewHolder).myTime.setText(stringDatum);
                }
                else
                {
                    ((MyViewHolder) viewHolder).myMessage.setText(message);
                    ((MyViewHolder) viewHolder).myTime.setText(stringDatum);
                }

                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                if(lataitude!=null&&longtitude!=null)
                {
                    ((TheirViewHolder) viewHolder).messageBody.setText("Hey, "+recipent+ " it´s my current location, please click on");
                    ((TheirViewHolder) viewHolder).theirName.setText(sender + "         sent on:" + stringDatum);
                }else {
                    ((TheirViewHolder) viewHolder).messageBody.setText(message);
                    ((TheirViewHolder) viewHolder).theirName.setText(sender + "         sent on:" + stringDatum);
                }
                break;

                case VIEW_TYPE_MESSAGE_MAP:
                {

                 break;

                }
            case VIEW_TYPE_PICTURE_RECEIVED:

                byte[] decodedString1 = Base64.decode(message, 12);
                Bitmap decodedByte1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
                ((TheirPictureViewHolder) viewHolder).img_recived.setImageBitmap(decodedByte1);
                ((TheirPictureViewHolder) viewHolder).theirname.setText(sender+"         sent on:" + stringDatum);
                break;

            case VIEW_TYPE_PICTURE_SENT:
                byte[] decodedString2 = Base64.decode(message, 12);
                Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
                ((myPictureViewHolder) viewHolder).my_time.setText(stringDatum);
                ((myPictureViewHolder) viewHolder).img_sent.setImageBitmap(decodedByte2);
                break;
        }


    }

    public int getItemViewType(int position) {
        Datum singleMessage;
        String sender;
        sender = list.get(position).getSender();


        if (list.get(position).getMimetype().equals(("gps"))){
            int index = list.get(position).getData().indexOf("|");

            lataitude = list.get(position).getData().substring(0, index - 1);
            longtitude = list.get(position).getData().substring(index + 1);

            if (sender.equals(senderName)) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }

        } else if (list.get(position).getMimetype().equals("file")){
            if(sender.equals(senderName)){
                return VIEW_TYPE_PICTURE_SENT;
            }
            else{
                return VIEW_TYPE_PICTURE_RECEIVED;
            }
        }
        else{
            lataitude=null;
            longtitude=null;
                if (sender.equals(senderName)) {
                    // If the current user is the sender of the message
                    return VIEW_TYPE_MESSAGE_SENT;
                } else {
                    // If some other user sent the message
                    return VIEW_TYPE_MESSAGE_RECEIVED;
                }
            }

        }






    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  // User Item, der bei Initialisierung erzeugt wird und der Platz für hält
    {


        GetMessageAdapter.OnFriendListener onFriendListener;
        TextView myMessage;
        TextView myTime;
        public MyViewHolder(View itemView, GetMessageAdapter.OnFriendListener onFriendListener)
        {
            super (itemView);
            myMessage = (TextView) itemView.findViewById(R.id.my_message_body);
            myTime = (TextView) itemView.findViewById(R.id.my_time);
            this.onFriendListener = onFriendListener;
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {

            onFriendListener.onNoteClick(getAdapterPosition());
        }
    }



    public static class TheirViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {


        GetMessageAdapter.OnFriendListener onFriendListener;
        TextView messageBody;
        TextView theirName;

        public TheirViewHolder(View itemView, GetMessageAdapter.OnFriendListener onFriendListener)
        {
            super(itemView);
            messageBody = (TextView) itemView.findViewById(R.id.message_body);
            theirName = (TextView) itemView.findViewById(R.id.their_name);
            this.onFriendListener = onFriendListener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

            onFriendListener.onNoteClick(getAdapterPosition());
        }
    }

    public static class TheirPictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        GetMessageAdapter.OnFriendListener onFriendListener;
        //TextView messageBody;
        TextView theirname;
        ImageView img_recived;
        public TheirPictureViewHolder(View itemView, GetMessageAdapter.OnFriendListener onFriendListener)
        {
            super(itemView);
            //messageBody = (TextView) itemView.findViewById(R.id.message_body);
            theirname = (TextView) itemView.findViewById(R.id.theirname);
            img_recived= itemView.findViewById(R.id.picture_recived);
            this.onFriendListener = onFriendListener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

            onFriendListener.onNoteClick(getAdapterPosition());
        }
    }
    public static class myPictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        GetMessageAdapter.OnFriendListener onFriendListener;
        //TextView messageBody;
        TextView my_time;
        ImageView img_sent;
        public myPictureViewHolder(View itemView, GetMessageAdapter.OnFriendListener onFriendListener)
        {
            super(itemView);
            //messageBody = (TextView) itemView.findViewById(R.id.message_body);
            my_time = (TextView) itemView.findViewById(R.id.time_sent);
            img_sent= itemView.findViewById(R.id.picture_sent);
            this.onFriendListener = onFriendListener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

            onFriendListener.onNoteClick(getAdapterPosition());
        }
    }


     public static class MapsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
     {

         GetMessageAdapter.OnFriendListener onFriendListener;
     ImageButton mapButton;
     TextView textViewLatitude;
     TextView textViewLongtitude;

     public MapsViewHolder(View itemView, GetMessageAdapter.OnFriendListener onFriendListener)
     {
     super(itemView);
     mapButton = (ImageButton) itemView.findViewById(R.id.mappeButton);
     textViewLatitude = (TextView) itemView.findViewById(R.id.mapTextViewLatitude);
     textViewLongtitude =(TextView)itemView.findViewById(R.id.mapTextViewLongtitude);
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