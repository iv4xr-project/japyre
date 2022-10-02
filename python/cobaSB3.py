import gym
from stable_baselines3 import PPO

#
# Just for trying out Stable-baseline-3 Reinforcement Learning lib.
# 
# Just run this script. It will learn a model of problem, then use the learned 
# model. You will see some visualization too :)
#


env = gym.make("CartPole-v1")

model = PPO("MlpPolicy", env, verbose=1)
model.learn(total_timesteps=10_000)

obs = env.reset()
for i in range(1000):
    action, _states = model.predict(obs, deterministic=True)
    obs, reward, done, info = env.step(action)
    env.render()
    if done:
      obs = env.reset()

env.close()
