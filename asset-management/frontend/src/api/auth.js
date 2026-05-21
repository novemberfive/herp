import request from './request'

// 用户登录
export function login(username, password) {
  return request({
    url: '/auth/login',
    method: 'post',
    params: { username, password }
  })
}

// 刷新 Token
export function refreshToken(refreshToken) {
  return request({
    url: '/auth/refresh',
    method: 'post',
    params: { refreshToken }
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/auth/userinfo'
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
