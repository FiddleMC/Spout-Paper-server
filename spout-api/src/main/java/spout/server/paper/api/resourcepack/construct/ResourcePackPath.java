package spout.server.paper.api.resourcepack.construct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import spout.server.paper.api.resourcepack.content.Blockstates;
import spout.server.paper.api.resourcepack.content.Lang;

/**
 * A path in the Spout server resource pack.
 */
public interface ResourcePackPath {

    /**
     * @return Whether a file exists at this path.
     */
    boolean exists();

    /**
     * Deletes the file at this path, if it existed.
     */
    void delete();

    /**
     * @return A view that allows treating the file contents at this path as a byte array.
     */
    BytesResourcePackPath asBytes();

    /**
     * @return A view that allows treating the file contents at this path as a string.
     */
    StringResourcePackPath asString();

    /**
     * @return A viewx that allows treating the file contents at this path as a {@link JsonElement}.
     */
    JsonElementResourcePackPath asJsonElement();

    /**
     * @return A view that allows treating the file contents at this path as a {@link JsonObject}.
     */
    JsonObjectResourcePackPath asJsonObject();

    /**
     * @return A view that allows treating the file contents at this path as a {@link Blockstates}.
     */
    BlockstatesResourcePackPath asBlockstates();

    /**
     * @return A view that allows treating the file contents at this path as a {@link Lang}.
     */
    LangResourcePackPath asLang();

}
