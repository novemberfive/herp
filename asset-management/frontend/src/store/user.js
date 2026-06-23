import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/auth'
import { ROLE_PERMISSION_MAP, expandPermissions } from '@/constants/permissions'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    refreshToken: localStorage.getItem('refreshToken') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo.username || '',
    realName: (state) => state.userInfo.realName || '',
    role: (state) => state.userInfo.role || '',
    permissions: (state) => expandPermissions(state.userInfo.permissions?.length ? state.userInfo.permissions : (ROLE_PERMISSION_MAP[state.userInfo.role] || [])),
    hasPermission: (state) => (permission) => {
      if (!permission) return true
      const permissions = expandPermissions(state.userInfo.permissions?.length ? state.userInfo.permissions : (ROLE_PERMISSION_MAP[state.userInfo.role] || []))
      return permissions.includes(permission)
    },
    hasAnyPermission: (state) => (permissionsToCheck = []) => {
      const permissions = expandPermissions(state.userInfo.permissions?.length ? state.userInfo.permissions : (ROLE_PERMISSION_MAP[state.userInfo.role] || []))
      return permissionsToCheck.some(permission => permissions.includes(permission))
    }
  },
  
  actions: {
    async loginAction(username, password) {
      const res = await login(username, password)
      this.token = res.data.token
      this.refreshToken = res.data.refreshToken
      this.userInfo = res.data.userInfo
      
      localStorage.setItem('token', this.token)
      localStorage.setItem('refreshToken', this.refreshToken)
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      
      return res
    },
    
    async getUserInfoAction() {
      const res = await getUserInfo()
      this.userInfo = res.data
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      return res
    },
    
    async logoutAction() {
      await logout()
      this.token = ''
      this.refreshToken = ''
      this.userInfo = {}
      
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
    },
    
    setToken(token, refreshToken) {
      this.token = token
      this.refreshToken = refreshToken
      localStorage.setItem('token', token)
      localStorage.setItem('refreshToken', refreshToken)
    }
  }
})
