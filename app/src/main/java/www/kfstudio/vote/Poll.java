package www.kfstudio.vote;

public class Poll {
    private String poll_1_url;
    private String poll_2_url;
    private String poll_3_url;
    private String poll_profile_image;
    private String poll_name;
    private String poll_email;
    private String poll_title;
    private String poll_1;
    private String poll_2;
    private String poll_3;
    private String poll_phone;

    public String getPoll_1_url() {
        return poll_1_url;
    }

    public String getPoll_2_url() {
        return poll_2_url;
    }

    public String getPoll_3_url() {
        return poll_3_url;
    }
    public String getPoll_profile_image() {
        return poll_profile_image;
    }



    public Poll(){

    }

    public String getPoll_name() {
        return poll_name;
    }

    public String getPoll_email() {
        return poll_email;
    }

    public String getPoll_title() {
        return poll_title;
    }

    public String getPoll_1() {
        return poll_1;
    }

    public String getPoll_2() {
        return poll_2;
    }

    public String getPoll_3() {
        return poll_3;
    }

    public Poll(String poll_name, String poll_email, String poll_title, String poll_1, String poll_2, String poll_3) {
        this.poll_name = poll_name;
        this.poll_email = poll_email;
        this.poll_title = poll_title;
        this.poll_1 = poll_1;
        this.poll_2 = poll_2;
        this.poll_3 = poll_3;
    }

    public String getPoll_phone() {
        return poll_phone;
    }
}
