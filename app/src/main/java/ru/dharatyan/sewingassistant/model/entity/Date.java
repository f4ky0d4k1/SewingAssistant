package ru.dharatyan.sewingassistant.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class Date implements Parcelable {

    private int year;
    private int month;
    private int day;

    private static final int[] daysInMonths = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public Date(Parcel parcel) {
        year = parcel.readInt();
        month = parcel.readInt();
        day = parcel.readInt();
    }

    public Date() {
        LocalDate localDate = LocalDate.now();
        year = localDate.getYear();
        month = localDate.getMonthValue();
        day = localDate.getDayOfMonth();
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(),"%d-%d-%d",year ,month ,day);
    }

    public static Date parse(String string) {
        String[] list = string.split("-");
        return new Date(Integer.parseInt(list[0]), Integer.parseInt(list[1]), Integer.parseInt(list[2]));
    }

    public static int daysInMonth(int month, int year) {
        if (year % 4 == 0 && month == 1) return 29;
        return daysInMonths[month - 1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
    }

    public static final Parcelable.Creator<Date> CREATOR = new Parcelable.Creator<Date>() {

        @Override
        public Date createFromParcel(Parcel parcel) {
            return new Date(parcel);
        }

        @Override
        public Date[] newArray(int size) {
            return new Date[0];
        }
    };
}
