package org.fiddlemc.fiddle.impl.resourcepack.construct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fiddlemc.fiddle.api.resourcepack.construct.BytesFiddleResourcePackPath;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackPath;
import org.fiddlemc.fiddle.api.resourcepack.construct.JsonElementFiddleResourcePackPath;
import org.fiddlemc.fiddle.api.resourcepack.construct.JsonObjectFiddleResourcePackPath;
import org.fiddlemc.fiddle.api.resourcepack.construct.StringFiddleResourcePackPath;
import org.jspecify.annotations.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * The implementation for {@link FiddleResourcePackPath}.
 */
public final class FiddleResourcePackPathImpl implements FiddleResourcePackPath {

    private abstract class View implements FiddleResourcePackPath {

        @Override
        public boolean exists() {
            return FiddleResourcePackPathImpl.this.exists();
        }

        @Override
        public void delete() {
            FiddleResourcePackPathImpl.this.delete();
        }

        @Override
        public BytesFiddleResourcePackPath asBytes() {
            return FiddleResourcePackPathImpl.this.asBytes();
        }

        @Override
        public StringFiddleResourcePackPath asString() {
            return FiddleResourcePackPathImpl.this.asString();
        }

        @Override
        public JsonElementFiddleResourcePackPath asJsonElement() {
            return FiddleResourcePackPathImpl.this.asJsonElement();
        }

        @Override
        public JsonObjectFiddleResourcePackPath asJsonObject() {
            return FiddleResourcePackPathImpl.this.asJsonObject();
        }

    }

    /**
     * The event that this instance was constructed for.
     */
    private final FiddleResourcePackConstructEventImpl event;

    /**
     * The string path this instance represents.
     */
    private final String path;

    /**
     * Whether a file at this path exists.
     */
    private boolean exists;

    FiddleResourcePackPathImpl(FiddleResourcePackConstructEventImpl event, String path) {
        this.event = event;
        this.path = path;
    }

    @Override
    public boolean exists() {
        return this.exists;
    }

    @Override
    public void delete() {
        this.exists = false;
        this.clear();
    }

    /**
     * @throws IllegalStateException If {@link #exists} is false.
     */
    private void throwExceptionIfNot(boolean value, String reason) {
        if (!value) {
            throw new IllegalStateException("File at this resource pack path " + reason + ": " + this.path);
        }
    }

    /**
     * @throws IllegalStateException If {@link #exists} is false.
     */
    private void throwExceptionIfNotExists() {
        this.throwExceptionIfNot(this.exists(), "does not exist");
    }

    /**
     * Sets all fields containing the data in this file to null.
     */
    private void clear() {
        this.bytes = null;
        this.string = null;
        this.jsonElement = null;
        this.jsonObject = null;
    }

    /**
     * The file contents as a byte array,
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    private byte @Nullable [] bytes;

    /**
     * The {@link BytesFiddleResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable BytesView bytesView;

    @Override
    public BytesFiddleResourcePackPath asBytes() {
        if (this.bytesView == null) {
            this.bytesView = new BytesView();
        }
        return this.bytesView;
    }

    private void setBytesFromContents() {
        if (this.string != null || this.jsonElement != null || this.jsonObject != null) {
            this.bytes = this.getString().getBytes();
        }
    }

    private byte[] getBytesImmutable() {
        this.throwExceptionIfNotExists();
        if (this.bytes == null) {
            // Find the contents and turn it into bytes
            this.setBytesFromContents();
        }
        return this.bytes;
    }

    private void setBytesMutable(byte[] bytes) {
        this.exists = true;
        this.clear();
        this.bytes = bytes;
    }

    private void updateBytes(Function<byte @Nullable [], byte @Nullable []> function) {
        byte @Nullable [] oldValue = this.exists ? this.getBytesImmutable() : null;
        byte @Nullable [] newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setBytesMutable(newValue);
        } else {
            this.delete();
        }
    }

    private class BytesView extends View implements BytesFiddleResourcePackPath {

        @Override
        public byte[] getImmutable() {
            return FiddleResourcePackPathImpl.this.getBytesImmutable();
        }

        @Override
        public void setMutable(byte[] bytes) {
            FiddleResourcePackPathImpl.this.setBytesMutable(bytes);
        }

        @Override
        public void update(Function<byte @Nullable [], byte @Nullable []> function) {
            FiddleResourcePackPathImpl.this.updateBytes(function);
        }

    }

    /**
     * The file contents as a string,
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable String string;

    /**
     * The {@link StringFiddleResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable StringView stringView;

    @Override
    public StringFiddleResourcePackPath asString() {
        if (this.stringView == null) {
            this.stringView = new StringView();
        }
        return this.stringView;
    }

    private void setStringFromContents() {
        if (this.bytes != null) {
            this.string = new String(this.bytes, StandardCharsets.UTF_8);
            return;
        }
        if (this.jsonElement != null || this.jsonObject != null) {
            this.string = this.getJsonElementImmutable().toString();
        }
    }

    private String getString() {
        this.throwExceptionIfNotExists();
        if (this.string == null) {
            // Find the contents and turn it into a string
            this.setStringFromContents();
        }
        return this.string;
    }

    private void setString(String string) {
        this.exists = true;
        this.clear();
        this.string = string;
    }

    private void updateString(Function<@Nullable String, @Nullable String> function) {
        @Nullable String oldValue = this.exists ? this.getString() : null;
        @Nullable String newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setString(newValue);
        } else {
            this.delete();
        }
    }

    private class StringView extends View implements StringFiddleResourcePackPath {

        @Override
        public String get() {
            return FiddleResourcePackPathImpl.this.getString();
        }

        @Override
        public void set(String string) {
            FiddleResourcePackPathImpl.this.setString(string);
        }

        @Override
        public void update(Function<@Nullable String, @Nullable String> function) {
            FiddleResourcePackPathImpl.this.updateString(function);
        }

    }

    /**
     * The file contents as a {@link JsonElement},
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable JsonElement jsonElement;

    /**
     * The {@link JsonElementFiddleResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable JsonElementFiddleResourcePackPath jsonElementView;

    @Override
    public JsonElementFiddleResourcePackPath asJsonElement() {
        if (this.jsonElementView == null) {
            this.jsonElementView = new JsonElementView();
        }
        return this.jsonElementView;
    }

    private void trySetJsonElementFromContents() {
        if (this.jsonObject != null) {
            this.jsonElement = this.jsonObject;
            return;
        }
        if (this.bytes != null || this.string != null) {
            try {
                this.jsonElement = JsonParser.parseString(this.getString());
            } catch (Exception ignored) {
            }
        }
    }

    private boolean isJsonElement() {
        if (this.jsonElement == null) {
            // Find the contents and turn it into a JsonElement
            this.trySetJsonElementFromContents();
        }
        return this.jsonElement != null;
    }

    private JsonElement getJsonElementImmutable() {
        this.throwExceptionIfNot(this.isJsonElement(), "is not valid JSON");
        return this.jsonElement;
    }

    private void setJsonElementMutable(JsonElement jsonElement) {
        this.exists = true;
        this.clear();
        this.jsonElement = jsonElement;
    }

    private void updateJsonElement(Function<@Nullable JsonElement, @Nullable JsonElement> function) {
        @Nullable JsonElement oldValue = this.isJsonElement() ? this.getJsonElementImmutable() : null;
        @Nullable JsonElement newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setJsonElementMutable(newValue);
        } else {
            this.delete();
        }
    }

    private void setJsonElementParsedFromString(String json) {
        this.setJsonElementMutable(JsonParser.parseString(json));
    }

    private class JsonElementView extends View implements JsonElementFiddleResourcePackPath {

        @Override
        public boolean isJsonElement() {
            return FiddleResourcePackPathImpl.this.isJsonElement();
        }

        @Override
        public JsonElement getImmutable() {
            return FiddleResourcePackPathImpl.this.getJsonElementImmutable();
        }

        @Override
        public void setMutable(JsonElement jsonElement) {
            FiddleResourcePackPathImpl.this.setJsonElementMutable(jsonElement);
        }

        @Override
        public void update(Function<@Nullable JsonElement, @Nullable JsonElement> function) {
            FiddleResourcePackPathImpl.this.updateJsonElement(function);
        }

        @Override
        public void setParsedFromString(String json) {
            FiddleResourcePackPathImpl.this.setJsonElementParsedFromString(json);
        }

    }

    /**
     * The file contents as a {@link JsonObject},
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable JsonObject jsonObject;

    /**
     * The {@link JsonObjectFiddleResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable JsonObjectFiddleResourcePackPath jsonObjectView;

    @Override
    public JsonObjectFiddleResourcePackPath asJsonObject() {
        if (this.jsonObjectView == null) {
            this.jsonObjectView = new JsonObjectView();
        }
        return this.jsonObjectView;
    }

    private void trySetJsonObjectFromContents() {
        if (this.bytes != null || this.string != null || this.jsonElement != null) {
            if (this.isJsonElement()) {
                try {
                    this.jsonObject = this.getJsonElementImmutable().getAsJsonObject();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean isJsonObject() {
        if (this.jsonObject == null) {
            // Find the contents and turn it into a JsonObject
            this.trySetJsonObjectFromContents();
        }
        return this.jsonObject != null;
    }

    private JsonObject getJsonObjectImmutable() {
        this.throwExceptionIfNot(this.isJsonObject(), "is not a valid JSON object");
        return this.jsonObject;
    }

    private void setJsonObjectMutable(JsonObject jsonObject) {
        this.exists = true;
        this.clear();
        this.jsonObject = jsonObject;
    }

    private void updateJsonObject(Function<@Nullable JsonObject, @Nullable JsonObject> function) {
        @Nullable JsonObject oldValue = this.isJsonObject() ? this.getJsonObjectImmutable() : null;
        @Nullable JsonObject newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setJsonObjectMutable(newValue);
        } else {
            this.delete();
        }
    }

    private void setJsonObjectParsedFromString(String json) {
        this.setJsonObjectMutable(JsonParser.parseString(json).getAsJsonObject());
    }

    private class JsonObjectView extends View implements JsonObjectFiddleResourcePackPath {

        @Override
        public boolean isJsonObject() {
            return FiddleResourcePackPathImpl.this.isJsonObject();
        }

        @Override
        public JsonObject getImmutable() {
            return FiddleResourcePackPathImpl.this.getJsonObjectImmutable();
        }

        @Override
        public void setMutable(JsonObject jsonObject) {
            FiddleResourcePackPathImpl.this.setJsonObjectMutable(jsonObject);
        }

        @Override
        public void update(Function<@Nullable JsonObject, @Nullable JsonObject> function) {
            FiddleResourcePackPathImpl.this.updateJsonObject(function);
        }

        @Override
        public void setParsedFromString(String json) {
            FiddleResourcePackPathImpl.this.setJsonObjectParsedFromString(json);
        }

    }

}
