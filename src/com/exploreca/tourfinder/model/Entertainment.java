package com.exploreca.tourfinder.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.exploreca.tourfinder.EntertainmentMenu;

public class Entertainment implements Parcelable {
	private long id;
	private String price;
	private String title;
	private String description;
	private String image;	//title Image

	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	@Override
	public String toString() {
//		NumberFormat nf = NumberFormat.getCurrencyInstance();
//		return title;
		return title + "\n" + price;
	}
    public Entertainment() {
    }

    public Entertainment(Parcel in) {
         Log.i(EntertainmentMenu.LOGTAG, "Parcel constructor");
        
         id = in.readLong();
         title = in.readString();
         description = in.readString();
         price = in.readString();
         image = in.readString();
    }

    @Override
    public int describeContents() {
         return 0;
    }
   
    @Override
    public void writeToParcel(Parcel dest, int flags) {
         Log.i(EntertainmentMenu.LOGTAG, "writeToParcel");
        
         dest.writeLong(id);
         dest.writeString(title);
         dest.writeString(description);
         dest.writeString(price);
         dest.writeString(image);
    }

    public static final Parcelable.Creator<Entertainment> CREATOR =
              new Parcelable.Creator<Entertainment>() {

         @Override
         public Entertainment createFromParcel(Parcel source) {
              Log.i(EntertainmentMenu.LOGTAG, "createFromParcel");
              return new Entertainment(source);
         }

         @Override
         public Entertainment[] newArray(int size) {
              Log.i(EntertainmentMenu.LOGTAG, "newArray");
              return new Entertainment[size];
         }

    };
}
