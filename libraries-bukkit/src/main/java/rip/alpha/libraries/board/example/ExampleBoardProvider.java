package rip.alpha.libraries.board.example;

import org.bukkit.entity.Player;
import rip.alpha.libraries.board.Board;
import rip.alpha.libraries.board.BoardProvider;
import rip.alpha.libraries.board.BoardUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ExampleBoardProvider implements BoardProvider {
    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add("&6&lHelloWorld");
        lines.add("&6&lRandom Number: &r" + ThreadLocalRandom.current().nextInt(100));
        lines.add("&4&lRandom Color &r" + BoardUtil.IDS[ThreadLocalRandom.current().nextInt(13)] + "Test");
        lines.add("&cUpdated every &r " + getUpdateInterval() + " ms");
        lines.add("&cIt can handle up to 15 lines");
        lines.add("&aAnd was made by PingzBoy");
        return lines;
    }

    boolean b = false;

    @Override
    public String getTitle(Player player) {
        b = !b;
        return b ? "Hey" : "Bye";
    }

    @Override
    public int getUpdateInterval() {
        return 1000;
    }

    @Override
    public void onBoardCreate(Player player, Board board) {

    }

    @Override
    public void onBoardUpdate(Player player, Board board) {

    }

    @Override
    public void onBoardRemove(Player player, Board board) {

    }
}
