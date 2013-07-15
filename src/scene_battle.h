/*
 * This file is part of EasyRPG Player.
 *
 * EasyRPG Player is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EasyRPG Player is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EasyRPG Player. If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef _SCENE_BATTLE_H_
#define _SCENE_BATTLE_H_

// Headers
#include <deque>
#include "rpg_troopmember.h"
#include "rpg_actor.h"
#include "rpg_enemy.h"
#include "game_actor.h"
#include "game_enemy.h"
#include "scene.h"
#include "background.h"
#include "drawable.h"
#include "zobj.h"
#include "window_help.h"
#include "window_item.h"
#include "window_skill.h"
#include "window_command.h"
#include "window_battleoption.h"
#include "window_battlecommand.h"
#include "window_battlestatus.h"
#include "window_message.h"
#include "battle_battler.h"
#include "battle_animation.h"
#include "spriteset_battle.h"
#include <boost/scoped_ptr.hpp>

namespace Battle {
class Action;
class SpriteAction;
}

namespace Game_BattleAlgorithm {
	class AlgorithmBase;
}

typedef EASYRPG_SHARED_PTR<Game_BattleAlgorithm::AlgorithmBase> BattleAlgorithmRef;

/**
 * Scene_Battle class.
 * Manages the battles.
 */
class Scene_Battle : public Scene {

public:
	static EASYRPG_SHARED_PTR<Scene_Battle> Create();

	~Scene_Battle();

	virtual void Start();
	virtual void Update();

	enum State {
		/** Battle has started (Display encounter message) */
		State_Start,
		/** Menu with Battle, Auto Battle and Escape Options */
		State_SelectOption,
		/** Selects next actor who has to move */
		State_SelectActor,
		/** Auto battle command selected */
		State_AutoBattle,
		/** Menu with abilities of current Actor (e.g. Command, Item, Skill and Defend) */
		State_SelectCommand,
		/** Item selection is active */
		State_SelectItem,
		/** Skill selection menu is active */
		State_SelectSkill,
		/** Player selects enemy target */
		State_SelectEnemyTarget,
		/** Player selects allied target */
		State_SelectAllyTarget,
		/** Battle Running */
		State_Battle,
		/** Battle Running, ally does move */
		State_AllyAction,
		/** Battle running, enemy does move */
		State_EnemyAction,
		/** Battle ended with a victory */
		State_Victory,
		/** Battle ended with a defeat */
		State_Defeat,
		/** Escape command selected */
		State_TryEscape
	};

	struct FloatText {
		FloatText(int x, int y, int color, const std::string& text, int duration);
		int duration;
		boost::scoped_ptr<Sprite> sprite;
	};

protected:
	Scene_Battle();

	friend class Battle::SpriteAction;

	virtual void InitBattleTest();

	virtual void CreateCursors();
	virtual void CreateWindows();

	virtual void CreateBattleOptionWindow() = 0;
	virtual void CreateBattleTargetWindow() = 0;
	virtual void CreateBattleCommandWindow() = 0;
	virtual void CreateBattleMessageWindow() = 0;

	virtual void ProcessActions() = 0;
	virtual void ProcessInput() = 0;

	virtual void SetState(Scene_Battle::State new_state) = 0;

	virtual void NextTurn();

	virtual void UpdateBackground();

	/**
	 * Convenience function, sets the animation state of the target if it has
	 * a valid battler sprite, does nothing otherwise.
	 *
	 * @param target Battler whose anim state is changed
	 * @param new_state new animation state
	 */
	virtual void SetAnimationState(Game_Battler* target, int new_state);

	// battle_algorithms.cpp

	void AttackEnemy(Battle::Ally& ally, Battle::Enemy& enemy);
	void UseItem(Battle::Ally& ally, const RPG::Item& item);
	void UseItemAlly(Battle::Ally& ally, const RPG::Item& item, Battle::Ally& target);
	void UseSkill(Battle::Ally& ally, const RPG::Skill& skill);
	void UseSkillAlly(Battle::Battler& ally, const RPG::Skill& skill, Battle::Battler& target);
	void UseSkillEnemy(Battle::Battler& ally, const RPG::Skill& skill, Battle::Battler& target);

	bool EnemyActionValid(const RPG::EnemyAction& action, Battle::Enemy& enemy);
	const RPG::EnemyAction* ChooseEnemyAction(Battle::Enemy& enemy);
	void EnemyAttackAlly(Battle::Enemy& enemy, Battle::Ally& ally);
	void EnemySkill(Battle::Enemy& enemy, const RPG::Skill& skill);

	// Variables
	State state;
	State previous_state;
	bool auto_battle;
	int cycle;
	int attack_state;
	int message_timer;
	const RPG::EnemyAction* enemy_action;
	std::deque<EASYRPG_SHARED_PTR<Battle::Action> > actions;
	int skill_id;
	int pending_command;


	int actor_index;
	Game_Actor* active_actor;

	/** Displays Fight, Autobattle, Flee */
	boost::scoped_ptr<Window_Command> options_window;
	/** Displays list of enemies */
	boost::scoped_ptr<Window_Command> target_window;
	/** Displays Attack, Defense, Magic, Item */
	boost::scoped_ptr<Window_Command> command_window;
	boost::scoped_ptr<Window_Item> item_window;
	boost::scoped_ptr<Window_Skill> skill_window;
	boost::scoped_ptr<Window_Help> help_window;
	/** Displays allies status */
	boost::scoped_ptr<Window_BattleStatus> status_window;
	boost::scoped_ptr<Window_Message> message_window;

	boost::scoped_ptr<Background> background;

	std::deque<BattleAlgorithmRef> battle_actions;
};

#endif
