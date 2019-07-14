package com.theoreticsinc.mobileparking;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Date;


/**
 *
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    OnDatePickedListener mCallback;
    Integer mLayoutId = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manualentry_date, container, false);
    }

    public interface OnDatePickedListener {
        public void onDatePicked(Integer mLayoutId, int year, int month, int date);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if (mCallback != null) {
            mCallback.onDatePicked(mLayoutId, i, i1, i2);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCallback = (OnDatePickedListener) getActivity();

        Bundle bundle = this.getArguments();
        mLayoutId = R.layout.fragment_manualentry_time;
        Date now = new Date();
        int year = 2019;
        int month = now.getMonth();
        int date = now.getDate();

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, date);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnDatePickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimePickedListener.");
        }
    }
}