package fr.utc.nf28.moka.io;

import java.util.List;

import fr.utc.nf28.moka.data.HistoryEntry;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

public interface MokaRestService {
	@GET("/history/list")
	void historyEntries(Callback<List<HistoryEntry>> cb);

	@GET("/item/list")
	void itemsEntries(Callback<Response> cb);

	@POST("/item/{id}/image")
	@Multipart
	void uploadPicture(@Path("id") int itemId, @Part("file") TypedFile uploadFile, Callback<Response> cb);
}