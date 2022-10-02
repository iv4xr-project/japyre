import gym
import numpy as np
from gym import spaces
from gymenv_client import GymEnvClient
from stable_baselines3 import PPO

class SqWorldEnv(gym.Env) :

    def __init__(self, worldSize):
        super(SqWorldEnv, self).__init__()
        self.size = worldSize
        self.javaGym = GymEnvClient("127.0.0.1",9999)
        self.action_space = spaces.Discrete(len(self.javaGym.actionSpace))
        self.observation_space = spaces.Box(low=-1, high=worldSize, shape=(1,), dtype=np.int8) 

    def reset(self):
        o = self.javaGym.reset()
        return np.array([o["x"], o["y"]])

    def close(self):
        self.javaGym.close()

    def step(self,action):
        o,rw,done,info = self.javaGym.step(self.javaGym.actionSpace[action])
        return (np.array([o["x"], o["y"]]), rw, done, info)

#model = PPO("MlpPolicy", env, verbose=1)
#model.learn(total_timesteps=10_000)

# just for testing:
if __name__ == '__main__':
    env = SqWorldEnv(6)
    env.reset()
    for k in range(10):
        print(f'[{k}] ---------------------')
        o,rew,done,_ = env.step(env.action_space.sample())
        print(f'Observation : {o}')
        print(f'Reward      : {rew}')
        print(f'Done        : {done}')
        if done: 
            break   
    env.close()    