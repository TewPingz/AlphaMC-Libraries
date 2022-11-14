package rip.alpha.libraries.gui;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rip.alpha.libraries.chatinput.ChatInput;
import rip.alpha.libraries.util.item.ItemBuilder;
import rip.alpha.libraries.util.message.MessageBuilder;
import rip.alpha.libraries.util.message.MessageTranslator;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedMenu extends Menu {

    private final String title;
    private final int elementsPerPage;
    private final List<Button> pageElements = new ArrayList<>();

    @Getter(AccessLevel.PROTECTED)
    private int currentPageIndex;

    public PaginatedMenu(String title, int elementsPerPage) {
        super();
        this.title = MessageTranslator.translate(title);
        this.elementsPerPage = (int) (Math.ceil((elementsPerPage) / 9D) * 9D);
        if (this.elementsPerPage > 36) {
            throw new IllegalArgumentException("The is too many elements per page, it can only be 36 and below");
        }
    }

    @Override
    protected Inventory createEmptyInventory(HumanEntity player) {
        int currentPage = this.currentPageIndex + 1;
        int maxPages = this.getPageAmount() + 1;
        String pageTitle = this.title + MessageTranslator.translate(" &7[" + currentPage + "/" + maxPages + "]");
        return Bukkit.createInventory(null, this.elementsPerPage + 9, pageTitle);
    }

    @Override
    public void setup(HumanEntity entity) {
        this.pageElements.clear();
        this.setupPaginatedMenu(entity);

        int startIndex = this.elementsPerPage * this.currentPageIndex;
        int maxElements = (this.elementsPerPage * this.currentPageIndex) + this.elementsPerPage;

        for (int i = startIndex; i < maxElements; i++) {
            if (i < 0 || i >= this.pageElements.size()) {
                continue;
            }
            this.addButton(this.pageElements.get(i));
        }

        for (int i = this.elementsPerPage; i < this.elementsPerPage + 9; i++) {
            if (this.getButtonForSlot(i) == null) {
                this.setButton(i, this.getNavigationPlaceholder());
            }
        }

        this.setButton(this.elementsPerPage, this.getPreviousButton());
        this.setButton(this.elementsPerPage + 4, this.getPageSelectorButton());
        this.setButton(this.elementsPerPage + 8, this.getNextButton());
    }

    private int getLastPageIndex() {
        return Math.floorDiv(this.pageElements.size() + this.elementsPerPage, this.elementsPerPage) - 1;
    }

    private int getPageAmount() {
        return this.getLastPageIndex();
    }

    protected void addPageElement(Button button) {
        this.pageElements.add(button);
    }

    private Button getNavigationPlaceholder() {
        return Button.builder()
                .itemCreator(player -> new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name("").build())
                .eventConsumer(event -> {
                })
                .build();
    }

    private Button getNextButton() {
        if (this.currentPageIndex >= this.getPageAmount()) {
            return this.getNavigationPlaceholder();
        }

        return Button.builder()
                .itemCreator(player -> this.getNextPageIcon())
                .eventConsumer(event -> {
                    this.currentPageIndex++;
                    this.open(event.getWhoClicked());
                }).build();
    }

    private Button getPreviousButton() {
        if (this.currentPageIndex <= 0) {
            return this.getNavigationPlaceholder();
        }

        return Button.builder()
                .itemCreator(player -> this.getPreviousPageIcon())
                .eventConsumer(event -> {
                    this.currentPageIndex--;
                    this.open(event.getWhoClicked());
                }).build();
    }

    private Button getPageSelectorButton() {
        return Button.builder()
                .itemCreator(player -> this.getPageSelectorIcon())
                .eventConsumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    player.closeInventory();
                    this.askForPage(player);
                }).build();
    }

    private void askForPage(Player player) {
        ChatInput.request(player, "What page would you like to skip to?", input -> {
            try {
                int pageToSkipTo = Integer.parseInt(input) - 1;

                if (pageToSkipTo < 0) {
                    player.sendMessage(MessageBuilder.constructError("{} was not found.", input));
                    this.askForPage(player);
                    return;
                }

                if (pageToSkipTo > this.getPageAmount()) {
                    player.sendMessage(MessageBuilder.constructError("{} was not found. Try under {}.", pageToSkipTo, this.getPageAmount() + 1));
                    this.askForPage(player);
                    return;
                }

                this.currentPageIndex = pageToSkipTo;
                this.open(player);
            } catch (NumberFormatException e) {
                player.sendMessage(MessageBuilder.constructError("{} is an invalid page number.", input));
                this.askForPage(player);
            }
        });
    }

    private ItemStack getNextPageIcon() {
        return new ItemBuilder(Material.BOOK).name("§eNext Page").build();
    }

    private ItemStack getPreviousPageIcon() {
        return new ItemBuilder(Material.BOOK).name("§ePrevious Page").build();
    }

    private ItemStack getPageSelectorIcon() {
        return new ItemBuilder(Material.BOOK_AND_QUILL).name("§ePage Selector").build();
    }

    protected abstract void setupPaginatedMenu(HumanEntity humanEntity);

}
