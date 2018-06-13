package minh.com.drinkshop.model;

/**
 * Created by sgvn144 on 2018/06/13.
 */

public class CheckUserResponse {
    private boolean exists;
    private String error_msg;

    public CheckUserResponse() {
    }

    public CheckUserResponse(boolean exists, String error_msg) {
        this.exists = exists;
        this.error_msg = error_msg;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
