package spout.common.moredatadriven.minecraft.common.dependent;

import net.minecraft.resources.Identifier;
import java.util.Collection;

/**
 * A data-driven resource that may be dependent on data-driven resources
 * of the same type, thereby imposing a required loading order.
 */
public interface DependentDataDrivenResource {

    Collection<Identifier> getRequiredResources();

}
