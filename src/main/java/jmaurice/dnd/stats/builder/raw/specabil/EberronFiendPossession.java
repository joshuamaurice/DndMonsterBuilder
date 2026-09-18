package jmaurice.dnd.stats.builder.raw.specabil;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class EberronFiendPossession extends BaseBuilder {

    public EberronFiendPossession(final Stats stats) { super(stats); }

    public void build() {
        stat("eberron fiendish possession ability").agg(root).to1("special abilities long", new Value("""
                <b>Eberron Fiendish Possession (SLA)</b><br/>
                As a standard action, the fiend possesses a creature within 5 ft.
                The fiend's body transforms into smoke and enters into the target creatures body, disappearing from view.
                Will negates; DC 10 + 1/2 fiend's Hit Dice + fiend's Charisma modifier.
                The caster level is equal to the fiend's Hit Dice.
                If a creature makes a successful save against a possession attempt, 
                then that creature is immune to that fiend's possession ability for 24 hours.
                <br/>
                    When a fiend successfully possesses a creature that was unaware of the fiend,
                the fiend may choose to keep the possessed creature unaware of the possession.
                The fiend may ride inside the possessed creature's body indefinitely 
                without the possessed creature becoming aware of the possession.
                The fiend automatically reads all of the surface thoughts of the posssessed creature, 
                as per the detect thoughts spell (no save).
                <br/>
                    A fiend possessing a creature cannot be harmed normally.
                Attacks directed at the possessed creature affect the possessed creature and not the fiend,
                including mental attacks such as dominate monster.
                A possession cannot be ended by a simple <i>dispel magic</i> spell.
                <br/>
                    While possessing a creature, the fiend cannot take any action, even purely mental actions, 
                with the following exceptions.
                <ul>
                <li>
                <i>End The Possession:</i> The fiend may choose to end the possession at any time, 
                returning to their normal form in an adjacent space.
                </li>
                <li>
                <i>Communicate With Host:</i> The fiend may communicate telepathically with the possessed creature in any common language.
                </li>
                <li>
                <i>Probe Thoughts:</i> The fiend may attempt to probe the possessed creature's memories
                as per the probe thoughts spell, Will negates; 
                a successful save reveals the fiend's possession to the possessed creature.
                </li>
                <li>
                <i>Empowering The Possessed Creature:</i>
                The fiend may choose to empower the possessed creature,
                granting a profane bonus to all ability scores (except Intelligence scores of 2 or less)
                of the possessed creature equal to 1/2 of the fiend's Hit Dice,
                plus a profane bonus to natural armor of the possessed creature equal to 1/4 of the fiend's Hit Dice.
                This buff may be granted or revoked at any time by the fiend with a standard action.
                (The fiend may not empower the possessed creature at the same time as attempting to control the possessed creature's body.)
                </li>
                <li>
                <i>Controlling The Possessed Creature's Body:</i> 
                The fiend may choose to exercise full control over the possessed creature's body.
                The possessed creature makes a Will saving throw once per round to resist this control.
                On a success, the possessed creature is staggered for the round but may otherwise act normally for that round.
                After three consecutive successful saves, the possession ends and the fiend is expelled from the possessed creature's body.
                After three consecutive failed saves, the posseessed creature may not resist this control for 24 hours.
                <br/>
                    While controlling the host body, use the following rules to determine 
                the effective statistics and abilities of the possessed creature.
                    <ul>
                    <li>
                        The fiend uses the raw physical statistics and abilities of the host body.
                        This includes the host creature's shape, size, reach, movement modes and speeds, natural weapons, 
                        strength score, dexterity score, constitution score, hit points,
                        base Reflex save bonus, base Fortitude save bonus,
                        and most racial skill bonuses.
                    </li>
                    <li>
                        The fiend uses their mental statistics and abilities.
                        This includes their intelligence score, wisdom score, charisma score,
                        their own base Will save bonus,
                        feats (including racial bonus feats), skill ranks, and class skills.
                    </li>
                    <li>
                        The fiend can use all of their spells and spell-like abilities.
                        The fiend cannot use any spells or spell-like abilities of the host creature,
                        but any magical effects already active on the host body continue to function as normal.
                    </li>
                    <li>
                        The fiend can use their own special abilities (supernatural and extraordinary)
                        which do not depend on their natural form.
                        The fiend can use any special abilities (supernatural and extraordinary) of the host creature
                        which are intrinsic to the host creature's current form,
                        such as a medusa's petrification gaze, a dragon's breath weapon, or a special touch attack.
                    </li>
                    <li>
                        The fiend does not gain access to the memories of the possessed creature,
                        and thus the fiend cannot use any ability of the host creature which requires extensive training to use.
                        This includes most abilities gained from class levels,
                        including a monk's improved unarmed strike, a monk's evasion, a rogue's sneak attack, and a wizard's spells.
                        However, the fiend benefits from most always-on passive abilities of the host creature 
                        which were gained from class levels,
                        such as most kinds of damage reduction, spell resistance, energy resistance, and energy immunity.
                    </li>
                    <li>
                        For alignment-specific effects, use the creature counts as the host's normal alignment or the fiend's alignment, whichever is worse.
                    </li> 
                    </ul>
                </li>
                </ul>
                """
                ));
    }

}
