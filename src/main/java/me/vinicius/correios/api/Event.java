package me.vinicius.correios.api;

public class Event {

    private String data;
    private String local;
    private String action;
    private String movement;

    public String getData() {
        return data;
    }

    public String getLocal() {
        return local;
    }

    public String getAction() {
        return action;
    }

    public String getMovement() {
        return movement;
    }

    Event(String data, String local, String action, String movement) {

        this.data = data;
        this.local = local;
        this.action = action;
        this.movement = movement;

    }
    @SuppressWarnings("unused")
    public String toSring() {
        return data + "\n" +
                local + "\n" +
                movement + "\n" +
                action;
    }

}
