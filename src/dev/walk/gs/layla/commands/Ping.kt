package dev.walk.gs.layla.commands

import dev.walk.gs.layla.Cooldown_Value
import dev.walk.gs.layla.events.CommandManager

class PingCommand : CommandManager() {

    override fun onCommand() {
        var time = System.currentTimeMillis()
        embed.setAuthor("Esperando resposta...")
        channel!!.sendMessage(embed.build())
                .queue { response -> response.editMessage(embed.clear().setAuthor("\uD83C\uDFD3 Pong! | Shard: ${jda!!.shardInfo.shardId}").setDescription("\uD83D\uDCF6 Tempo de resposta: ${event!!.jda.gatewayPing} ms\n:speaking_head: Api: ${(System.currentTimeMillis() - time)} ms").build()).queue() }
        cooldown!!.createCooldown(Cooldown_Value)
    }

}