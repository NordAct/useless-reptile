Backport of some features and improvements from 0.11.0:
- Major pathfinding improvements for all dragons. Now poor Pikehorns should get stuck underwater much less often, as well as others in other not convenient places
- Moleclaw's strong attack box Y coordinate change is now clamped and changes on either +1 or -1 if you're either looking up or down and pass certain threshold in pitch angle when riding it
- Fixed Moleclaw strong attack box block breaking area being inconsistent
- When giving either food item or taming item to dragon, its remnant (such as bucket from bucket with fish) will correctly be placed in current slot in case if it was last item in the stack instead of slipping in other first available slot in inventory
- Rideable dragons no longer can be leashed
- When bailing out, Lightning Chaser no longer should attempt to pathfind into unloaded chunks. This should fix some scenarios where this behaviour caused too much lag
- Added 2 new modes for Flute: Sit Down and Stand Up. Each mode sits down or stands up all River Pikehorns in flute work radius respectively