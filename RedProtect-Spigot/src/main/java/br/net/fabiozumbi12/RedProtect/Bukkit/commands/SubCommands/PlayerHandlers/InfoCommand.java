/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 02/07/2020 18:59.
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package br.net.fabiozumbi12.RedProtect.Bukkit.commands.SubCommands.PlayerHandlers;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import br.net.fabiozumbi12.RedProtect.Bukkit.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.net.fabiozumbi12.RedProtect.Bukkit.commands.CommandHandlers.handleInfo;
import static br.net.fabiozumbi12.RedProtect.Bukkit.commands.CommandHandlers.handleInfoTop;

public class InfoCommand implements SubCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2 && (sender instanceof ConsoleCommandSender || RedProtect.get().getPermissionHandler().hasPerm(sender, "redprotect.command.admin.info"))) {
            if (Bukkit.getWorld(args[1]) != null) {
                Region r = RedProtect.get().getRegionManager().getRegion(args[0], Bukkit.getWorld(args[1]).getName());
                if (r != null) {
                    sender.sendMessage(RedProtect.get().getLanguageManager().get("general.color") + "-----------------------------------------");
                    sender.sendMessage(r.info());
                    sender.sendMessage(RedProtect.get().getLanguageManager().get("general.color") + "-----------------------------------------");
                } else {
                    RedProtect.get().getLanguageManager().sendMessage(sender, RedProtect.get().getLanguageManager().get("correct.usage") + " " + ChatColor.YELLOW + "Invalid region: " + args[0]);
                }
            } else {
                RedProtect.get().getLanguageManager().sendMessage(sender, RedProtect.get().getLanguageManager().get("correct.usage") + " " + ChatColor.YELLOW + "Invalid World: " + args[1]);
            }
            return true;
        } else if (sender instanceof Player player) {

            //rp info [region] [world]
            if (args.length == 0) {
                handleInfoTop(player);
                return true;
            }
            if (args.length == 1) {
                handleInfo(player, args[0], "");
                return true;
            }
            if (args.length == 2) {
                handleInfo(player, args[0], args[1]);
                return true;
            }
        }

        RedProtect.get().getLanguageManager().sendCommandHelp(sender, "info", true);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 2) {
            if (args[1].isEmpty())
                tab.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
            else
                tab.addAll(Bukkit.getWorlds().stream().filter(e -> e.getName().startsWith(args[1])).map(World::getName).collect(Collectors.toList()));
        }
        return tab;
    }
}