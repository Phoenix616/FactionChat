package nz.co.lolnet.james137137.FactionChat;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @author James
 */
public class FactionChatListener2 implements Listener {

    private ChatChannel2 channel;
    private OtherChatChannel otherChannel;
    private FactionChat FactionChat;
    static final Logger log = Bukkit.getLogger();
    public ScoreboardManager scoreboardmanager;

    protected FactionChatListener2(FactionChat FactionChat) {
        scoreboardmanager = Bukkit.getScoreboardManager();
        this.FactionChat = FactionChat;
        if (FactionChat.FactionsEnable) {
            channel = new ChatChannel2(FactionChat);
        }
        otherChannel = new OtherChatChannel(FactionChat);

    }

    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ChatMode.SetNewChatMode(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    protected void onPlayerChat(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) {
            return;
        }
        Player talkingPlayer = event.getPlayer();
        String msg = event.getMessage();
        //FPlayer me = (FPlayer)FPlayers.i.get(talkingPlayer);
        String chatmode = ChatMode.getChatMode(talkingPlayer);
        if (!chatmode.equalsIgnoreCase("PUBLIC")) {
            boolean isFactionChat = false;
            if (FactionChat.FactionsEnable) {
                if (chatmode.equalsIgnoreCase("ALLY&TRUCE")) {
                    channel.fChatAT(talkingPlayer, msg);
                    event.setCancelled(true);
                    isFactionChat = true;

                } else if (chatmode.equalsIgnoreCase("ENEMY")) {
                    channel.fChatE(talkingPlayer, msg);
                    event.setCancelled(true);
                    isFactionChat = true;
                } else if (chatmode.equalsIgnoreCase("FACTION")) {
                    channel.fChatF(talkingPlayer, msg);
                    event.setCancelled(true);
                    isFactionChat = true;
                } else if (chatmode.equalsIgnoreCase("ALLY")) {
                    channel.fChatA(talkingPlayer, msg);
                    event.setCancelled(true);
                    isFactionChat = true;
                } else if (chatmode.equalsIgnoreCase("TRUCE")) {
                    channel.fChatTruce(talkingPlayer, msg);
                    event.setCancelled(true);
                    isFactionChat = true;
                } else if (chatmode.equalsIgnoreCase("LEADER")) {
                    channel.fChatLeader(talkingPlayer, msg);
                    event.setCancelled(true);
                    isFactionChat = true;
                } else if (chatmode.equalsIgnoreCase("OFFICER")) {
                    channel.fChatOfficer(talkingPlayer, msg);
                    event.setCancelled(true);
                    isFactionChat = true;
                }

                if (isFactionChat) {
                    log.log(Level.INFO, "[FactionChat] {0}|{1}: {2}", new Object[]{chatmode, talkingPlayer.getName(), msg});
                    return;
                }

            }

            if (chatmode.equalsIgnoreCase("UserAssistant")) {
                otherChannel.userAssistantChat(talkingPlayer, msg);
                event.setCancelled(true);
            } else if (chatmode.equalsIgnoreCase("JrMOD")) {
                otherChannel.jrModChat(talkingPlayer, msg);
                event.setCancelled(true);
            } else if (chatmode.equalsIgnoreCase("MOD")) {
                otherChannel.modChat(talkingPlayer, msg);
                event.setCancelled(true);
            } else if (chatmode.equalsIgnoreCase("SrMOD")) {
                otherChannel.SrModChat(talkingPlayer, msg);
                event.setCancelled(true);
            } else if (chatmode.equalsIgnoreCase("JrAdmin")) {
                otherChannel.JrAdminChat(talkingPlayer, msg);
                event.setCancelled(true);
            } else if (chatmode.equalsIgnoreCase("ADMIN")) {
                otherChannel.adminChat(talkingPlayer, msg);
                event.setCancelled(true);
            }

            log.log(Level.INFO, "[FactionChat] {0}|{1}: {2}", new Object[]{chatmode, talkingPlayer.getName(), msg});
        }
        else
        {
            if (ChatMode.mutePublicOptionEnabled)
            {
                for (final Player player : FactionChat.getServer().getOnlinePlayers()) {
                    if (ChatMode.IsPublicMuted(player)) {
                        
                        if (player.getName().equals(talkingPlayer.getName()))
                        {
                            ChatMode.MutePublicOption(player);
                        }
                        else
                        {
                           event.getRecipients().remove(player); 
                        }
                        
                    }
                }
            }
            
          
        }


    }

    @EventHandler(priority = EventPriority.LOWEST)
    protected void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String message = event.getMessage();
        String[] split = message.split(" ");
        if (split.length < 2) {
            return;
        }
        if (split[0].equalsIgnoreCase("/factions") || split[0].equalsIgnoreCase(("/" + FactionChat.FactionsCommand))) {
            if (split[1].equalsIgnoreCase("chat") || split[1].equalsIgnoreCase("c")) {
                Player player = event.getPlayer();
                String senderFaction = channel.getFactionName(player);
                if (senderFaction.contains("Wilderness") && !player.hasPermission("FactionChat.UserAssistantChat")
                        && !FactionChat.isDebugger(player.getName())) {
                    //checks if player is in a faction
                    //mangaddp juniormoderators FactionChat.JrModChat
                    player.sendMessage(ChatColor.RED + FactionChat.messageNotInFaction);
                    return;
                }
                if (split.length >= 3) {
                    ChatMode.setChatMode(player, split[2]);
                } else {
                    ChatMode.NextChatMode(player);
                }
                event.setCancelled(true);
            }
        }
    }
}