package com.vithushan.therottengame.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.vithushan.therottengame.model.MediaModel;

import java.io.IOException;

/**
 * Created by vnama on 7/8/2015.
 */
public class CreditNameAdapter extends TypeAdapter<MediaModel> {

        @Override
        public MediaModel read(final JsonReader in) throws IOException {
            final MediaModel myClassInstance = new MediaModel();

            in.beginObject();
            while (in.hasNext()) {
                String jsonTag = in.nextName();
                if ("id".equals(jsonTag)) {
                    myClassInstance.id = in.nextInt();
                } else if ("name".equals(jsonTag)
                        || "title".equals(jsonTag)) {
                    myClassInstance.name = in.nextString();
                }
            }
            in.endObject();

            return myClassInstance;
        }

        @Override
        public void write(final JsonWriter out, final MediaModel myClassInstance)
                throws IOException {
            out.beginObject();
            out.name("id").value(myClassInstance.id);
            out.name("poster_path").value(myClassInstance.posterPath);
            out.name("name").value(myClassInstance.name);
            out.name("media_type").value(myClassInstance.type.toString());
            out.endObject();
        }

}

