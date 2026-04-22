package spout.server.paper.impl.resourcepack.construct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spout.server.paper.api.resourcepack.construct.BlockstatesResourcePackPath;
import spout.server.paper.api.resourcepack.construct.BytesResourcePackPath;
import spout.server.paper.api.resourcepack.construct.ResourcePackPath;
import spout.server.paper.api.resourcepack.construct.JsonElementResourcePackPath;
import spout.server.paper.api.resourcepack.construct.JsonObjectResourcePackPath;
import spout.server.paper.api.resourcepack.construct.LangResourcePackPath;
import spout.server.paper.api.resourcepack.construct.StringResourcePackPath;
import spout.server.paper.api.resourcepack.content.Blockstates;
import spout.server.paper.api.resourcepack.content.Lang;
import org.jspecify.annotations.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * The implementation for {@link ResourcePackPath}.
 */
public final class ResourcePackPathImpl implements ResourcePackPath {

    private abstract class View implements ResourcePackPath {

        @Override
        public boolean exists() {
            return ResourcePackPathImpl.this.exists();
        }

        @Override
        public void delete() {
            ResourcePackPathImpl.this.delete();
        }

        @Override
        public BytesResourcePackPath asBytes() {
            return ResourcePackPathImpl.this.asBytes();
        }

        @Override
        public StringResourcePackPath asString() {
            return ResourcePackPathImpl.this.asString();
        }

        @Override
        public JsonElementResourcePackPath asJsonElement() {
            return ResourcePackPathImpl.this.asJsonElement();
        }

        @Override
        public JsonObjectResourcePackPath asJsonObject() {
            return ResourcePackPathImpl.this.asJsonObject();
        }

        @Override
        public BlockstatesResourcePackPath asBlockstates() {
            return ResourcePackPathImpl.this.asBlockstates();
        }

        @Override
        public LangResourcePackPath asLang() {
            return ResourcePackPathImpl.this.asLang();
        }

    }

    /**
     * The event that this instance was constructed for.
     */
    private final ResourcePackConstructEventImpl event;

    /**
     * The string path this instance represents.
     */
    private final String path;

    /**
     * Whether a file at this path exists.
     */
    private boolean exists;

    ResourcePackPathImpl(ResourcePackConstructEventImpl event, String path) {
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
        this.blockstates = null;
        this.lang = null;
    }

    /**
     * The file contents as a byte array,
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    private byte @Nullable [] bytes;

    /**
     * The {@link BytesResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable BytesView bytesView;

    @Override
    public BytesResourcePackPath asBytes() {
        if (this.bytesView == null) {
            this.bytesView = new BytesView();
        }
        return this.bytesView;
    }

    private void setBytesFromContents() {
        if (this.string != null || this.jsonElement != null || this.jsonObject != null || this.blockstates != null || this.lang != null) {
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

    private class BytesView extends View implements BytesResourcePackPath {

        @Override
        public byte[] getImmutable() {
            return ResourcePackPathImpl.this.getBytesImmutable();
        }

        @Override
        public void setMutable(byte[] bytes) {
            ResourcePackPathImpl.this.setBytesMutable(bytes);
        }

        @Override
        public void update(Function<byte @Nullable [], byte @Nullable []> function) {
            ResourcePackPathImpl.this.updateBytes(function);
        }

    }

    /**
     * The file contents as a string,
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable String string;

    /**
     * The {@link StringResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable StringView stringView;

    @Override
    public StringResourcePackPath asString() {
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
        if (this.jsonElement != null || this.jsonObject != null || this.blockstates != null || this.lang != null) {
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

    private class StringView extends View implements StringResourcePackPath {

        @Override
        public String get() {
            return ResourcePackPathImpl.this.getString();
        }

        @Override
        public void set(String string) {
            ResourcePackPathImpl.this.setString(string);
        }

        @Override
        public void update(Function<@Nullable String, @Nullable String> function) {
            ResourcePackPathImpl.this.updateString(function);
        }

    }

    /**
     * The file contents as a {@link JsonElement},
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable JsonElement jsonElement;

    /**
     * The {@link JsonElementResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable JsonElementResourcePackPath jsonElementView;

    @Override
    public JsonElementResourcePackPath asJsonElement() {
        if (this.jsonElementView == null) {
            this.jsonElementView = new JsonElementView();
        }
        return this.jsonElementView;
    }

    private void trySetJsonElementFromContents() {
        if (this.jsonObject != null || this.blockstates != null || this.lang != null) {
            this.jsonElement = this.getJsonObjectImmutable();
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

    private class JsonElementView extends View implements JsonElementResourcePackPath {

        @Override
        public boolean isJsonElement() {
            return ResourcePackPathImpl.this.isJsonElement();
        }

        @Override
        public JsonElement getImmutable() {
            return ResourcePackPathImpl.this.getJsonElementImmutable();
        }

        @Override
        public void setMutable(JsonElement jsonElement) {
            ResourcePackPathImpl.this.setJsonElementMutable(jsonElement);
        }

        @Override
        public void update(Function<@Nullable JsonElement, @Nullable JsonElement> function) {
            ResourcePackPathImpl.this.updateJsonElement(function);
        }

        @Override
        public void setParsedFromString(String json) {
            ResourcePackPathImpl.this.setJsonElementParsedFromString(json);
        }

    }

    /**
     * The file contents as a {@link JsonObject},
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable JsonObject jsonObject;

    /**
     * The {@link JsonObjectResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable JsonObjectResourcePackPath jsonObjectView;

    @Override
    public JsonObjectResourcePackPath asJsonObject() {
        if (this.jsonObjectView == null) {
            this.jsonObjectView = new JsonObjectView();
        }
        return this.jsonObjectView;
    }

    private void trySetJsonObjectFromContents() {
        if (this.blockstates != null ) {
            this.jsonObject = this.getBlockstatesImmutable().getJson();
            return;
        }
        if (this.lang != null) {
            this.jsonObject = this.getLangImmutable().getJson();
            return;
        }
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

    private class JsonObjectView extends View implements JsonObjectResourcePackPath {

        @Override
        public boolean isJsonObject() {
            return ResourcePackPathImpl.this.isJsonObject();
        }

        @Override
        public JsonObject getImmutable() {
            return ResourcePackPathImpl.this.getJsonObjectImmutable();
        }

        @Override
        public void setMutable(JsonObject jsonObject) {
            ResourcePackPathImpl.this.setJsonObjectMutable(jsonObject);
        }

        @Override
        public void update(Function<@Nullable JsonObject, @Nullable JsonObject> function) {
            ResourcePackPathImpl.this.updateJsonObject(function);
        }

        @Override
        public void setParsedFromString(String json) {
            ResourcePackPathImpl.this.setJsonObjectParsedFromString(json);
        }

    }

    /**
     * The file contents as a {@link Blockstates},
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable Blockstates blockstates;

    /**
     * The {@link BlockstatesResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable BlockstatesView blockstatesView;

    @Override
    public BlockstatesResourcePackPath asBlockstates() {
        if (this.blockstatesView == null) {
            this.blockstatesView = new BlockstatesView();
        }
        return this.blockstatesView;
    }

    private void trySetBlockstatesFromContents() {
        if (this.bytes != null || this.string != null || this.jsonElement != null || this.jsonObject != null) {
            if (this.isJsonObject()) {
                try {
                    this.blockstates = Blockstates.ofMutable(this.getJsonObjectImmutable());
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean isBlockstates() {
        if (this.blockstates == null) {
            // Find the contents and turn it into a Blockstates
            this.trySetBlockstatesFromContents();
        }
        return this.blockstates != null;
    }

    private Blockstates getBlockstatesImmutable() {
        this.throwExceptionIfNot(this.isBlockstates(), "is not a valid blockstates file");
        return this.blockstates;
    }

    private void setBlockstatesMutable(Blockstates blockstates) {
        this.exists = true;
        this.clear();
        this.blockstates = blockstates;
    }

    private void updateBlockstates(Function<@Nullable Blockstates, @Nullable Blockstates> function) {
        @Nullable Blockstates oldValue = this.isBlockstates() ? this.getBlockstatesImmutable() : null;
        @Nullable Blockstates newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setBlockstatesMutable(newValue);
        } else {
            this.delete();
        }
    }

    private class BlockstatesView extends View implements BlockstatesResourcePackPath {

        @Override
        public boolean isBlockstates() {
            return ResourcePackPathImpl.this.isBlockstates();
        }

        @Override
        public Blockstates getImmutable() {
            return ResourcePackPathImpl.this.getBlockstatesImmutable();
        }

        @Override
        public void setMutable(Blockstates blockstates) {
            ResourcePackPathImpl.this.setBlockstatesMutable(blockstates);
        }

        @Override
        public void update(Function<@Nullable Blockstates, @Nullable Blockstates> function) {
            ResourcePackPathImpl.this.updateBlockstates(function);
        }

    }

    /**
     * The file contents as a {@link Lang},
     * or null if it is not up-to-date,
     * or null if {@link #exists} is false.
     */
    @Nullable Lang lang;

    /**
     * The {@link LangResourcePackPath} view of this instance,
     * or null if not initialized yet.
     */
    private @Nullable LangView langView;

    @Override
    public LangResourcePackPath asLang() {
        if (this.langView == null) {
            this.langView = new LangView();
        }
        return this.langView;
    }

    private void trySetLangFromContents() {
        if (this.bytes != null || this.string != null || this.jsonElement != null || this.jsonObject != null) {
            if (this.isJsonObject()) {
                try {
                    this.lang = Lang.wrap(this.getJsonObjectImmutable());
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean isLang() {
        if (this.lang == null) {
            // Find the contents and turn it into a Lang
            this.trySetLangFromContents();
        }
        return this.lang != null;
    }

    private Lang getLangImmutable() {
        this.throwExceptionIfNot(this.isLang(), "is not a valid lang file");
        return this.lang;
    }

    private void setLangMutable(Lang lang) {
        this.exists = true;
        this.clear();
        this.lang = lang;
    }

    private void updateLang(Function<@Nullable Lang, @Nullable Lang> function) {
        @Nullable Lang oldValue = this.isLang() ? this.getLangImmutable() : null;
        @Nullable Lang newValue = function.apply(oldValue);
        if (newValue != null) {
            this.setLangMutable(newValue);
        } else {
            this.delete();
        }
    }

    private class LangView extends View implements LangResourcePackPath {

        @Override
        public boolean isLang() {
            return ResourcePackPathImpl.this.isLang();
        }

        @Override
        public Lang getImmutable() {
            return ResourcePackPathImpl.this.getLangImmutable();
        }

        @Override
        public void setMutable(Lang lang) {
            ResourcePackPathImpl.this.setLangMutable(lang);
        }

        @Override
        public void update(Function<@Nullable Lang, @Nullable Lang> function) {
            ResourcePackPathImpl.this.updateLang(function);
        }

    }

}
