package leppa.iterationpermutation.research;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.Map;

public interface Researcher{
	
	/**
	 * Returns the last index of page section unlocked for that research.
	 * Returns 0 for researches that have not been unlocked yet, or have no progress.
	 *
	 * @param page
	 * 		The research page to check the status of.
	 * @return The last index of page section unlocked, or 0 if it hasn't been unlocked or progressed.
	 */
	int stage(ResearchPage page);
	
	/**
	 * Increments the stage for a page.
	 *
	 * <p>If the new section has no requirements, this continues to increment the stage
	 * until it reaches either a section with requirements, or the end of the research.
	 *
	 * <p>Fires {@link ResearchAdvancedEvent} if the page is not already complete.
	 *
	 * <p>Has no effect if the page is already complete.
	 *
	 * <p>TODO: addenda.
	 *
	 * @param page
	 * 		The research page to advance.
	 */
	void advance(ResearchPage page);
	
	/**
	 * Sets this researchers progress for a page to zero.
	 *
	 * @param page
	 * 		The research page to reset.
	 */
	void reset(ResearchPage page);
	
	/**
	 * Sets this researchers progress for a page to its maximum progress
	 *
	 * <p>Has no effect if this research is already complete.
	 *
	 * @param page
	 * 		The research page to complete.
	 */
	void complete(ResearchPage page);
	
	/**
	 * Returns a map containing this researcher's data, where the keys are the keys of all sections
	 * that have a stage greater than -1, and the values are the current stage of that research.
	 *
	 * @return A Map containing the data of this researcher.
	 */
	Map<String, Integer> copyData();
	
	/**
	 * Sets the data of this researcher to the data of another.
	 *
	 * <p>All calls to <code>stage</code> should return the same values that <code>other</code> would
	 * at the time of copying, and should not be affected by calls to <code>advance</code> on <code>other</code>.
	 *
	 * @param other
	 * 		The researcher to copy from.
	 */
	void setData(Researcher other);
	
	/**
	 * Returns an NBT compound tag containing this researcher's data, according to this implementation.
	 *
	 * @return A compound tag containing this researcher's data.
	 */
	CompoundNBT serialize();
	
	/**
	 * Restores data previously serialized using <code>serialize</code> on an instance of this implementation.
	 *
	 * @param nbt
	 * 		A compound data to restore data from.
	 */
	void deserialize(CompoundNBT nbt);
	
	/**
	 * Gets a player's researcher capability, or throws an IllegalStateException if there is no attached researcher capability.
	 *
	 * @param p
	 * 		The player to get a capability from.
	 * @return The player's researcher capability.
	 * @throws IllegalStateException
	 * 		If the player doesn't have an attached researcher capability.
	 */
	static Researcher getFrom(PlayerEntity p){
		// An exception should *not* be thrown, since every player gets one, but just in case.
		return p.getCapability(ResearchCapability.PROVIDER_CAPABILITY, null).orElseThrow(IllegalStateException::new);
	}
	
	static boolean canAdvance(PlayerEntity player, ResearchPage page){
		Researcher researcher = Researcher.getFrom(player);
		return page.sections().get(researcher.stage(page)).getRequirements().stream().allMatch(x -> x.satisfiedBy(player));
	}
	
	static void advanceAndTake(PlayerEntity player, ResearchPage page){
		if(canAdvance(player, page)){
			Researcher researcher = Researcher.getFrom(player);
			page.sections().get(researcher.stage(page)).getRequirements().forEach(x -> x.take(player));
			researcher.advance(page);
		}
	}
}