package de.uni_due.paluno.chuj;

import de.uni_due.paluno.chuj.Models.Anmeldungsantwort;
import de.uni_due.paluno.chuj.Models.GetMessages;
import de.uni_due.paluno.chuj.Models.GetMessagesAntwort;
import de.uni_due.paluno.chuj.Models.PushToken;
import de.uni_due.paluno.chuj.Models.RemoveFriend;
import de.uni_due.paluno.chuj.Models.User;
import de.uni_due.paluno.chuj.Models.AddFriend;
import de.uni_due.paluno.chuj.Models.MessageModel;
import de.uni_due.paluno.chuj.Models.MessageResponse;
import de.uni_due.paluno.chuj.Models.Password;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/api/user/register")
    Call<Anmeldungsantwort> registerUser(@Body User user);

    @POST("/api/user/validate")
    Call<Anmeldungsantwort> loginUser(@Body User user );

    @POST("/api/friends/add")
    Call<Anmeldungsantwort> addFriend(@Body AddFriend addFriend);

    @POST("/api/friends/get")
    Call<Anmeldungsantwort> getFriends(@Body User user);

    @POST("/api/message/send")
    Call<MessageResponse> sendMessage(@Body MessageModel messageModel);

    @POST("/api/message/get")
    Call<GetMessagesAntwort> getMessages(@Body GetMessages getMessages);

    @POST("/api/friends/remove")
    Call<Anmeldungsantwort> deleteFriend(@Body RemoveFriend removeFriend);

    @POST("/api/user/password")
    Call<Anmeldungsantwort> changePassword(@Body Password password);

    @POST("/api/user/pushtoken")
    Call<Anmeldungsantwort> changePushToken(@Body PushToken pushToken);

}

