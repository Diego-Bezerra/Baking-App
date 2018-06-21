package br.com.bezerra.diego.bakingapp.gui.detailsActivity.step;

import android.os.Parcel;
import android.os.Parcelable;

public class StepFragmentBundle implements Parcelable {

    private long stepId;
    private int stepPosition;
    private Long nextStepId;
    private Integer nextStepPosition;
    private String recipeTitle;

    public StepFragmentBundle(long stepId, int stepPosition, Long nextStepId, Integer nextStepPosition, String recipeTitle) {
        this.stepId = stepId;
        this.stepPosition = stepPosition;
        this.nextStepId = nextStepId;
        this.nextStepPosition = nextStepPosition;
        this.recipeTitle = recipeTitle;
    }

    protected StepFragmentBundle(Parcel in) {
        stepId = in.readLong();
        stepPosition = in.readInt();
        if (in.readByte() == 0) {
            nextStepId = null;
        } else {
            nextStepId = in.readLong();
        }
        if (in.readByte() == 0) {
            nextStepPosition = null;
        } else {
            nextStepPosition = in.readInt();
        }
        recipeTitle = in.readString();
    }

    public static final Creator<StepFragmentBundle> CREATOR = new Creator<StepFragmentBundle>() {
        @Override
        public StepFragmentBundle createFromParcel(Parcel in) {
            return new StepFragmentBundle(in);
        }

        @Override
        public StepFragmentBundle[] newArray(int size) {
            return new StepFragmentBundle[size];
        }
    };

    public long getStepId() {
        return stepId;
    }

    public void setStepId(long stepId) {
        this.stepId = stepId;
    }

    public int getStepPosition() {
        return stepPosition;
    }

    public void setStepPosition(int stepPosition) {
        this.stepPosition = stepPosition;
    }

    public Long getNextStepId() {
        return nextStepId;
    }

    public void setNextStepId(Long nextStepId) {
        this.nextStepId = nextStepId;
    }

    public Integer getNextStepPosition() {
        return nextStepPosition;
    }

    public void setNextStepPosition(Integer nextStepPosition) {
        this.nextStepPosition = nextStepPosition;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(stepId);
        dest.writeInt(stepPosition);
        if (nextStepId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(nextStepId);
        }
        if (nextStepPosition == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(nextStepPosition);
        }
        dest.writeString(recipeTitle);
    }
}
