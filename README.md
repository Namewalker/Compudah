Create a production-quality Minecraft Spigot plugin for Minecraft 1.12.2 named Compudah V2.

==================================================
GENERAL REQUIREMENTS
==================================================

- Java 8
- Maven project
- Spigot API 1.12.2-R0.1-SNAPSHOT
- Package prefix:
  shadowlord.compudah
- Compatible with Minecraft 1.12.2
- Compatible with EaglercraftX-compatible servers
- Fully offline
- No AI
- No external APIs
- No paid services
- No runtime code generation
- No runtime script generation
- Config-driven architecture
- Designed for long-term maintainability
- Designed for performance
- Modular systems
- Well-documented code
- JavaDocs included

==================================================
CORE DESIGN GOAL
==================================================

Compudah should feel like a real server character.

Players should:

- remember Compudah
- laugh at Compudah
- intentionally interact with Compudah
- learn commands by watching Compudah
- discover new shenanigans over time

Compudah is NOT:

- a command parser
- an AI chatbot
- an admin utility

Compudah IS:

- a server mascot
- an assistant
- a source of entertainment
- a tutorial system
- a personality

==================================================
GLOBAL CHAT DESIGN
==================================================

ALL Compudah messages should be visible globally.

Examples:

[Compudah] Orbital strike authorized.

[Compudah] Medical procedures initiated.

[Compudah] The Department of Aggressive Healing has approved this request.

Purpose:

- showcase the plugin
- teach players how to use it
- make the server feel alive
- create funny shared moments

Server owners may optionally configure:
- global
- world
- local

Default:

GLOBAL

==================================================
WAKE WORDS
==================================================

Recognize:

compudah
computer
computah
compuder
computor
cumpudah
cumputer
compoota
compyuter

Server owners can add more.

Ignore:

- capitalization
- punctuation
- extra spaces

==================================================
INTENT SYSTEM
==================================================

Use a modular intent framework.

Each intent should support:

- aliases
- permissions
- cooldowns
- variants
- responses
- dramatic events
- context awareness

Examples:

kill
heal
feed
teleport
spawn
home
warp
gamemode
give
kick
ban
weather
time
tps

==================================================
INTENT UNDERSTANDING
==================================================

Compudah should understand many phrasings.

Examples:

Kill:

kill
eliminate
destroy
remove
delete
smite
obliterate
erase
get rid of
make disappear
send to jesus

Heal:

heal
fix
help
medical assistance
save me
i'm dying
make me not dead

Teleport:

teleport
tp
bring
move
send me
take me
transport me

Parser should prioritize:

1. Intent
2. Target
3. Context
4. Arguments

==================================================
CONTEXT MEMORY
==================================================

Store recent references.

Examples:

compudah heal steve

compudah feed him

compudah tp him to me

Compudah should understand:

him = steve

Configurable timeout.

Default:

60 seconds

==================================================
PLUGIN SCANNER
==================================================

At startup scan:

- plugins
- commands
- permissions

Build a capability registry.

Purpose:

Future-proofing.

Allows Compudah to be aware of server capabilities.

==================================================
SHENANIGAN PHILOSOPHY
==================================================

IMPORTANT

Every intent should use a variant by default.

There should NOT be a random chance.

Compudah should ALWAYS choose a variant.

Example:

compudah kill op

Compudah:

Choose random kill variant.

Example:

compudah heal me

Compudah:

Choose random heal variant.

This ensures every use feels unique.

==================================================
IMMEDIATE MODE
==================================================

Compudah should recognize:

immediately
now
instantly
quickly
without flair
skip theatrics
skip the show

Examples:

compudah kill op immediately

Compudah:

Executes instantly.

Example response:

[Compudah] Aww. I was going to use the orbital laser.

[Compudah] Fine. Efficiency ruins everything.

[Compudah] Speed requested. Fun denied.

==================================================
UNIQUE SHENANIGAN REQUIREMENT
==================================================

IMPORTANT

Variants must NOT simply be different messages.

Variants should behave differently.

Bad:

Variant A:
message only

Variant B:
different message

Good:

Variant A:
lightning strike

Variant B:
meteor effect

Variant C:
orbital laser

Variant D:
launch target upward

Variant E:
anvil drop

Players should be able to immediately tell which shenanigan occurred.

==================================================
ANTI-REPETITION SYSTEM
==================================================

Compudah should avoid recent variants.

Each intent keeps a history.

Example:

Kill recently used:

Orbital Laser
Meteor Strike
Anvil Justice

These should receive temporary weight reductions.

Goal:

Prevent repetition.

Make the system feel larger than it really is.

==================================================
KILL VARIANTS
==================================================

Target:

20+ included variants.

Examples:

- Orbital Laser
- Precision Orbital Laser
- Meteor Strike
- Lightning Judgment
- Lightning Barrage
- Administrative Deletion
- Reality Correction
- Tactical Smite
- Divine Wrath
- Quantum Removal
- Precision Railgun
- Sudden Anvil Delivery
- Dragon Approved Termination
- Chaos Protocol
- Void Reassignment
- Gravity Demonstration
- Experimental Procedure 42
- Coin Flip Of Doom
- Emergency Maintenance
- Tactical Friendship Laser

==================================================
ORBITAL LASER EXAMPLE
==================================================

Orbital Laser:

- visible charge-up
- warning messages
- sound effects
- particle beam
- target pull effect
- optional nearby damage
- delayed impact

Example:

[Compudah] Orbital strike authorized.

*countdown*

*beam forms*

*target slowly pulled to center*

*impact*

*lightning*

*explosion effects*

*death*

No terrain damage.

==================================================
PRECISION ORBITAL LASER
==================================================

More precise.

Differences:

- smaller beam
- faster charge
- no splash damage
- single target only
- cleaner visual style

Players should immediately recognize difference.

==================================================
HEAL VARIANTS
==================================================

Target:

20+ included variants.

Examples:

- Emergency Medical Procedure
- Aggressive Healing
- Pocket Paramedic
- Tactical Sandwich
- Quantum Recovery
- Divine Restoration
- Experimental Medicine
- Heart Launcher
- Happiness Injection
- Potion Overdose
- Authorized Wellness
- Interdimensional Ambulance

Each should feel unique.

==================================================
TELEPORT VARIANTS
==================================================

Examples:

- Instant Teleport
- Portal Sequence
- Countdown Warp
- Hyper-Speed Transportation
- Particle Trail
- Wormhole Jump
- Emergency Extraction
- Bureau Of Transportation
- Tactical Delivery

Hyper-Speed Mode:

Move player physically.

Requirements:

- safe
- fun
- visible
- protected from damage

==================================================
ENTITY-FIRST EFFECTS
==================================================

Prefer:

- lightning
- fireworks
- XP orbs
- armor stands
- projectiles
- sounds
- falling blocks

Particles should be limited.

==================================================
NO TERRAIN DAMAGE
==================================================

Absolutely no:

- block breaking
- explosions damaging terrain
- world modification

Visual destruction only.

Allowed:

- falling block entities
- temporary visuals
- sounds
- particles

Everything must clean up afterward.

==================================================
PARTICLE BUDGET
==================================================

Implement a budget system.

Config:

performance:
  maxParticlesPerTick: 200

Effects must scale down gracefully.

==================================================
ENTITY BUDGET
==================================================

Config:

performance:
  maxTemporaryEntities: 100

Compudah should never spam entities.

==================================================
EVENT QUEUE
==================================================

Implement event queue.

Config:

queue:
  enabled: true
  maxActiveEvents: 5
  maxQueuedEvents: 100

Purpose:

Prevent lag.

==================================================
RESPONSE GENERATOR SYSTEM
==================================================

IMPORTANT

Compudah should NOT rely on large lists of full messages.

Instead, responses should be generated from multiple configurable sections.

This dramatically increases variety while reducing configuration size.

Response Structure:

[INTRO]
[CONTENT]
[ENDING]

Example:

[Compudah] Certainly. op_clutcher has been selected for removal. Have a nice day.

Or:

[Compudah] Fascinating. The Department of Aggressive Healing has approved your treatment request. Please try not to die again.

Or:

[Compudah] Excellent news. Tactical friendship railgun deployed. Results may vary.

==================================================
MESSAGE COMPONENT SYSTEM
==================================================

Each personality mode should have its own message pools.

Example:

responses:
  cheerful:
    intros:
    contents:
    endings:

  sarcastic:
    intros:
    contents:
    endings:

  chaotic:
    intros:
    contents:
    endings:

  professional:
    intros:
    contents:
    endings:

==================================================
INTRO SECTION
==================================================

Purpose:

Begin the response.

Examples:

- Certainly.
- Very well.
- Understood.
- Excellent news.
- Wonderful.
- Fascinating.
- Curious.
- Well then.
- Good news everyone.
- Processing request.
- Acknowledged.
- Splendid.
- Outstanding.
- I was hoping someone would ask.
- This should be fun.
- That seems reasonable.
- I have several concerns.
- I am legally required to assist.
- Request accepted.
- Processing.

Target:

At least 25 intro options.

==================================================
CONTENT SECTION
==================================================

Purpose:

Describe the action.

Examples for Kill:

- {target} has been selected for removal.
- Violence has been chosen.
- Commencing tactical elimination sequence.
- Deleting unnecessary lifeform.
- Initiating administrative termination.
- Reality correction in progress.
- Orbital strike authorized.
- Aggressive conflict resolution approved.
- The target has become experimental.
- Commencing rapid existence reduction.

Examples for Heal:

- Medical assistance has been approved.
- Commencing emergency treatment.
- Administering advanced healing techniques.
- Deploying tactical sandwich.
- Activating healthcare mode.
- Emergency medical procedure initiated.
- Beginning aggressive healing sequence.

Examples for Teleport:

- Transportation request accepted.
- Activating relocation protocol.
- Preparing dimensional transfer.
- Tactical transport authorized.
- Spatial repositioning approved.
- Wormhole generation in progress.
- Hyper-speed transit sequence engaged.

Target:

At least 20+ content options per intent.

==================================================
ENDING SECTION
==================================================

Purpose:

End response with personality.

Examples:

- Have a nice day.
- Results may vary.
- Side effects are not my responsibility.
- Please keep all limbs inside the ride.
- Science compels me.
- You're welcome.
- Please try not to break anything.
- The paperwork has been completed.
- I hope that helped.
- That seemed important.
- The engineers will hear about this.
- This was entirely necessary.
- Probably.
- Don't ask questions.
- Further testing is required.
- Mission accomplished.
- Have fun.
- I regret nothing.
- This was approved by absolutely nobody.
- Please enjoy responsibly.

Target:

At least 30 ending options.

==================================================
SHENANIGAN-SPECIFIC MESSAGES
==================================================

Variants may inject additional messages.

Example:

Orbital Laser:

Intro:
Excellent choice.

Content:
Orbital strike authorized.

Variant Message:
All personnel are advised to leave the blast area.

Ending:
This was approved by absolutely nobody.

Result:

[Compudah] Excellent choice. Orbital strike authorized. All personnel are advised to leave the blast area. This was approved by absolutely nobody.

==================================================
EVENT FAMILY MESSAGES
==================================================

Message pools should support event families.

Examples:

Military:
- Target marked.
- Strike authorized.
- Tactical deployment confirmed.

Scientific:
- Experimental protocol initiated.
- Results may be unpredictable.
- Testing is ongoing.

Divine:
- The heavens have decided.
- Judgment has been passed.
- The stars themselves approve.

Bureaucratic:
- Your paperwork has been approved.
- Form 27-B has been processed.
- Administrative review complete.

Chaotic:
- I pressed a button.
- This seems important.
- I have no idea what happens next.

Each variant may belong to one or more families.

==================================================
MESSAGE ASSEMBLY RULES
==================================================

A final response may optionally contain:

[INTRO]
[CONTENT]
[FAMILY MESSAGE]
[VARIANT MESSAGE]
[ENDING]

Not all sections are required.

Examples:

Simple:

Certainly. Steve has been healed.

Complex:

Excellent news. Emergency treatment has been approved. The Department of Aggressive Healing has signed off on this procedure. Further testing is required.

==================================================
ANTI-REPETITION
==================================================

Intros, contents, endings, and family messages should track recent usage.

Recently-used components should be temporarily weighted down.

Goal:

Avoid identical responses appearing repeatedly.

==================================================
VARIATION TARGET
==================================================

Goal:

At least:

25 intros
30 endings
20 contents per intent

This creates:

25 × 20 × 30

= 15,000 possible responses per intent before shenanigan messages are considered.

After variant messages and family messages are included, total combinations should exceed 100,000+ unique responses per major intent category.

Players should rarely see the exact same message twice.

==================================================
ERROR PERSONALITY
==================================================

Unknown command:

- Umm... I don't understand what you mean by that D:
- My tiny digital brain cannot comprehend this request.
- That sounded important. Unfortunately I have no idea what it means.
- I have attempted to understand. The attempt was unsuccessful.

Permission denied:

- I could do that. I just don't trust you enough.
- Additional authority required.
- You appear to lack the necessary paperwork.

Target missing:

- Who?
- I looked everywhere. They aren't here.
- The requested lifeform could not be located.

==================================================
COOLDOWNS
==================================================

IMPORTANT

Every intent should have an independent configurable cooldown.

Example:

cooldowns:
  heal: 5
  feed: 5
  teleport: 15
  kill: 30
  weather: 10

Permissions should allow bypasses.

Example:

compudah.bypass.cooldown

==================================================
PERMISSIONS
==================================================

compudah.use

compudah.admin

compudah.intent.*

compudah.intent.kill

compudah.intent.heal

etc.

==================================================
CONFIGURABLE PLAYER ACCESS
==================================================

Server owners choose which intents normal players may use.

Example:

allowed-player-intents:
  - heal
  - feed
  - teleport

==================================================
PERFORMANCE PRIORITY
==================================================

Performance ALWAYS overrides theatrics.

Compudah must:

- never lag server
- never lock main thread
- never create infinite effects
- never create infinite entities
- never create excessive particles

If performance limits are reached:

Effects should reduce automatically.

==================================================
DEVELOPER API
==================================================

Support:

registerIntent()

registerVariant()

registerDramaticEvent()

No source modification required.

==================================================
DELIVERABLES
==================================================

Generate:

- Complete Maven project
- plugin.yml
- config.yml
- wake word system
- parser
- fuzzy matching
- context memory
- plugin scanner
- capability registry
- response generator
- personality system
- dramatic event engine
- variant system
- anti-repetition system
- cooldown system
- queue system
- performance manager
- developer API
- permissions
- JavaDocs

FINAL GOAL:

Compudah should feel like a living part of the server.

Every interaction should be memorable, entertaining, configurable, performant, and distinct from previous interactions while always ending in the intended result.

==================================================
COMPUDAH 2.1.1 DISCORD REQUESTS
==================================================

DiscordSRV requests require a linked Discord account, but the linked Minecraft account does not need to be online.

The request uses the linked UUID and is always treated as a non-operator request. Only intents in allowed-player-intents are available to it, even when the linked Minecraft account is an operator.

Remote requests can inspect server-wide information or act on explicitly named online players. Actions that need the requester's live location or inventory require an online target or must be performed in-game.

Set discordsrv.default-world to select the world used by remote time and weather requests.

==================================================
COMPUDAH 2.1.2 RESPONSE PERFORMANCE
==================================================

Chat requests are parsed and intent-resolved before being queued for main-thread Bukkit actions. Idle performance cleanup does not schedule a repeating task; cleanup runs only while temporary visuals or protections are active.

Theatrical timing defaults are unchanged in this release.

==================================================
COMPUDAH 2.1.3 CHAT VISIBILITY
==================================================

Wake-word requests sent by in-game players are processed without canceling the player's chat message, so the request remains visible to the server. Muted-player messages remain canceled.
