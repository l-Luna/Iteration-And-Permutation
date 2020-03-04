package leppa.iterationpermutation.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import leppa.iterationpermutation.PermutationNetwork;
import leppa.iterationpermutation.network.PktAdvanceResearch;
import leppa.iterationpermutation.network.PktCompleteResearch;
import leppa.iterationpermutation.network.PktResetResearch;
import leppa.iterationpermutation.research.Knowledges;
import leppa.iterationpermutation.research.ResearchPage;
import leppa.iterationpermutation.research.Researcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Collection;
import java.util.Optional;

public class ResearchCommand{
	
	// not final -- needs to be reset by Json
	private static SuggestionProvider<CommandSource> RESEARCH_SUGGESTER = (context, builder) ->
			ISuggestionProvider.func_212476_a(Knowledges.allPages().map(x -> new ResourceLocation(x.id())), builder);
	
	public static void resetSuggestionProvider(){
		RESEARCH_SUGGESTER = (context, builder) ->
				ISuggestionProvider.func_212476_a(Knowledges.allPages().map(x -> new ResourceLocation(x.id())), builder);
	}
	
	public static void register(CommandDispatcher<CommandSource> dispatcher){
		dispatcher.register(Commands.literal("iter-research").requires(source -> source.hasPermissionLevel(2))
				.then(
						Commands.literal("reset")
						.then(
								Commands.argument("targets", EntityArgument.players())
								.then(
										Commands.argument("research", ResourceLocationArgument.resourceLocation())
										.suggests(RESEARCH_SUGGESTER)
										.executes(context -> resetResearch(context.getSource(),
																		   ResourceLocationArgument.getResourceLocation(context, "research"),
																		   EntityArgument.getPlayers(context, "targets")))
								)
						)
				).then(
						Commands.literal("complete")
						.then(
								Commands.argument("targets", EntityArgument.players())
								.then(
										Commands.argument("research", ResourceLocationArgument.resourceLocation())
										.suggests(RESEARCH_SUGGESTER)
										.executes(context -> completeResearch(context.getSource(),
																			  ResourceLocationArgument.getResourceLocation(context, "research"),
																			  EntityArgument.getPlayers(context, "targets")))
								)
						)
				).then(
						Commands.literal("tryAdvance")
						.then(
								Commands.argument("targets", EntityArgument.players())
								.then(
										Commands.argument("research", ResourceLocationArgument.resourceLocation())
										.suggests(RESEARCH_SUGGESTER)
										.executes(context -> tryAdvanceResearch(context.getSource(),
																				ResourceLocationArgument.getResourceLocation(context, "research"),
																				EntityArgument.getPlayers(context, "targets")))
								)
						)
				).then(
						Commands.literal("forceAdvance")
						.then(
								Commands.argument("targets", EntityArgument.players())
								.then(
										Commands.argument("research", ResourceLocationArgument.resourceLocation())
										.suggests(RESEARCH_SUGGESTER)
										.executes(context -> forceAdvanceResearch(context.getSource(),
																				  ResourceLocationArgument.getResourceLocation(context, "research"),
																				  EntityArgument.getPlayers(context, "targets")))
								)
						)
				)
		);
	}
	
	private static int resetResearch(CommandSource source, ResourceLocation key, Collection<ServerPlayerEntity> targets){
		Optional<ResearchPage> optPage = Knowledges.pageForId(key.toString());
		if(optPage.isPresent()){
			ResearchPage page = optPage.get();
			targets.forEach(player -> {
				Researcher.getFrom(player).reset(page);
				PermutationNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PktResetResearch(key.toString()));
			});
			source.sendFeedback(new StringTextComponent("Reset " + key + " for " + targets + "."), true);
			return targets.size();
		}else{
			source.sendErrorMessage(new StringTextComponent("Could not find research " + key));
			return 0;
		}
	}
	
	private static int completeResearch(CommandSource source, ResourceLocation key, Collection<ServerPlayerEntity> targets){
		Optional<ResearchPage> optPage = Knowledges.pageForId(key.toString());
		if(optPage.isPresent()){
			ResearchPage page = optPage.get();
			targets.forEach(player -> {
				Researcher.getFrom(player).complete(page);
				PermutationNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PktCompleteResearch(key.toString()));
			});
			source.sendFeedback(new StringTextComponent("Completed " + key + " for " + targets + "."), true);
			return targets.size();
		}else{
			source.sendErrorMessage(new StringTextComponent("Could not find research " + key));
			return 0;
		}
	}
	
	private static int tryAdvanceResearch(CommandSource source, ResourceLocation key, Collection<ServerPlayerEntity> targets){
		Optional<ResearchPage> optPage = Knowledges.pageForId(key.toString());
		if(optPage.isPresent()){
			ResearchPage page = optPage.get();
			targets.forEach(player -> {
				if(Researcher.canAdvance(player, page)){
					Researcher.advanceAndTake(player, page);
					PermutationNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PktAdvanceResearch(key.toString()));
				}else{
					source.sendFeedback(new StringTextComponent("Didn't not advance research for " + player.getDisplayName() + ", since they don't satisfy requirements for research " + key), true);
				}
			});
			source.sendFeedback(new StringTextComponent("Advanced " + key + " for " + targets + "."), true);
			return targets.size();
		}else{
			source.sendErrorMessage(new StringTextComponent("Could not find research " + key));
			return 0;
		}
	}
	
	private static int forceAdvanceResearch(CommandSource source, ResourceLocation key, Collection<ServerPlayerEntity> targets){
		Optional<ResearchPage> optPage = Knowledges.pageForId(key.toString());
		if(optPage.isPresent()){
			ResearchPage page = optPage.get();
			targets.forEach(player -> {
				Researcher.getFrom(player).advance(page);
				PermutationNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PktAdvanceResearch(key.toString()));
			});
			source.sendFeedback(new StringTextComponent("Force advanced " + key + " for " + targets + "."), true);
			return targets.size();
		}else{
			source.sendErrorMessage(new StringTextComponent("Could not find research " + key));
			return 0;
		}
	}
}