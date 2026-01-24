package spireQuests.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;
import java.util.Iterator;

import static spireQuests.Anniv8Mod.makeID;

public class RemoveChoiceCardEffect extends AbstractGameEffect {
    private boolean cardsSelected;
    private static final float STARTING_DURATION = 0.5F;

    public RemoveChoiceCardEffect() {
        cardsSelected = false;
        startingDuration = duration = STARTING_DURATION;
    }

    public void update() {
        if(duration == startingDuration) {
            if (AbstractDungeon.isScreenUp) {
                AbstractDungeon.dynamicBanner.hide();
                AbstractDungeon.overlayMenu.cancelButton.hide();
                AbstractDungeon.previousScreen = AbstractDungeon.screen;
            }
            (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.INCOMPLETE;

            CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard card : (AbstractDungeon.player.masterDeck.getPurgeableCards()).group) {
                tmp.addToTop(card);
            }

            if (tmp.group.isEmpty()) {
                this.cardsSelected = true;
                return;
            }
            if (tmp.group.size() == 1) {
                deleteCards(tmp.group);
            } else {
                AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                        .getPurgeableCards(), 1, CardCrawlGame.languagePack.getUIString(makeID("QuestReward")).TEXT[7], false, false, false, true);
            }
        }
        duration -= Gdx.graphics.getDeltaTime();
        if (!this.cardsSelected &&
                AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
            deleteCards(AbstractDungeon.gridSelectScreen.selectedCards);
        }
    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }

    private void deleteCards(ArrayList<AbstractCard> group) {
        this.cardsSelected = true;
        for (Iterator<AbstractCard> i = group.iterator(); i.hasNext(); ) {
            AbstractCard card = i.next();
            card.untip();
            card.unhover();
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, Settings.WIDTH / 3.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.player.masterDeck.removeCard(card);
        }

        (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        this.isDone = true;
    }
}