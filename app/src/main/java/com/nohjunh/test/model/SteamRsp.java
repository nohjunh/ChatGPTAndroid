package com.nohjunh.test.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SteamRsp {

    @SerializedName("id")
    private String id;
    @SerializedName("object")
    private String object;
    @SerializedName("created")
    private long created;
    @SerializedName("model")
    private String model;
    @SerializedName("choices")
    private List<ChoicesDTO> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ChoicesDTO> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoicesDTO> choices) {
        this.choices = choices;
    }

    public static class ChoicesDTO {
        @SerializedName("delta")
        private DeltaDTO delta;
        @SerializedName("index")
        private int index;

        public DeltaDTO getDelta() {
            return delta;
        }

        public void setDelta(DeltaDTO delta) {
            this.delta = delta;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public static class DeltaDTO {
            @SerializedName("role")
            private String role;
            @SerializedName("content")
            private String content;

            public String getRole() {
                return role == null ? "" : role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public String getContent() {
                return content == null ? "" : content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
