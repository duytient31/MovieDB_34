package com.cris.nvh.moviedb.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nvh
 * Contact: toiyeuthethao1997@gmail.com
 */

public class Video implements Parcelable {
	@SerializedName("id")
	private String mId;
	@SerializedName("key")
	private String mKey;
	@SerializedName("name")
	private String mName;

	public Video(String id) {
		mId = id;
	}

	protected Video(Parcel in) {
		mId = in.readString();
		mKey = in.readString();
		mName = in.readString();
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(mId);
		parcel.writeString(mKey);
		parcel.writeString(mName);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Video> CREATOR = new Creator<Video>() {
		@Override
		public Video createFromParcel(Parcel in) {
			return new Video(in);
		}

		@Override
		public Video[] newArray(int size) {
			return new Video[size];
		}
	};

	public String getId() {
		return mId;
	}

	public String getKey() {
		return mKey;
	}

	public String getName() {
		return mName;
	}
}
