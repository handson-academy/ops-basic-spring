package com.handson.basic.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonAutoDetect
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RestErrorMessage {
    private String error;



    @JsonProperty
    public String getError() {
        return error;
    }


    @Override
    public String toString() {
        return "RestErrorMessage{" +
                "error='" + error + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestErrorMessage that = (RestErrorMessage) o;
        return Objects.equals(error, that.error) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(error);
    }


    public static final class RestErrorMessageBuilder {
        private String error;

        private RestErrorMessageBuilder() {
        }

        public static RestErrorMessageBuilder aRestErrorMessage() {
            return new RestErrorMessageBuilder();
        }

        public RestErrorMessageBuilder error(String error) {
            this.error = error;
            return this;
        }


        public RestErrorMessage build() {
            RestErrorMessage restErrorMessage = new RestErrorMessage();
            restErrorMessage.error = this.error;
            return restErrorMessage;
        }
    }
}
