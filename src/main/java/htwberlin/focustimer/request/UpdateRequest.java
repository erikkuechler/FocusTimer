package htwberlin.focustimer.request;

public class UpdateRequest {

    private String username;
    private String email;
    private String password;
    private String newPassword;
    private boolean delete;
    
    public UpdateRequest(String username, String email, String password, String newPassword, boolean delete) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.newPassword = newPassword;
        this.delete = delete;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

}
